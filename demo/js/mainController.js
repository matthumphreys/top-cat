app.controller("mainController", function($scope, $http) {
    $scope.posts = null;
    $scope.errorMessage = null;
    $scope.currentPost = null;
    $scope.currentPostIndex = -1;
    $scope.postHistory = null;
    $scope.categoryStats = {};
    $scope.globalStats = {hits: 0, actions: 0, actualProb: 30, softenedProb: 30};
    $scope.playlistVisible = true;
    $scope.historyVisible = false;
    $scope.trace = false;

    log = function(msg) {
        console.log(msg); // Uncomment for debug
    };

    $scope.init = function() {
        loadPostHistory();
        fetchRemotePosts();
    };

    loadPostHistory = function() {
        var history = localStorage['postHistory'];
        $scope.postHistory = (history) ? angular.fromJson(history) : [];
        updateProbs();
    };

    savePostHistory = function() {
        localStorage["postHistory"] = angular.toJson($scope.postHistory);
    };

    $scope.clearPostHistory = function() {
        localStorage.removeItem("postHistory");
        $scope.postHistory = [];
    }

    fetchRemotePosts = function() {
        var url = "http://d20o19rp6m6brr.cloudfront.net/news-feed/?callback=JSON_CALLBACK";
        log("Fetching remote data from " + url);
        $http.jsonp(url).success(function(data) {
            $scope.posts = initPosts(data.posts);
            updatePlaylist();
            setCurrentPost(0);
        }).error(function(error) {
            console.log(error);
            $scope.errorMessage = error;
        });
    };

    initPosts = function(posts) {
        for (var i = 0; (i < posts.length); i++) {
            var post = posts[i];
            post.originalIndex = i;
            post.positionIsFixed = false;
            post.position = 1000;   // Bigger than number of posts in session
            post.isHit = false;
            post.isMiss = false;
            post.title = unescape(post.title);
            post.catSlug = getCategoryFromPost(post);
        }
        return posts;
    };

    unescape = function(str) {
        var out = str.replace("&nbsp;", " ");
        out = out.replace("&#8211;", "-");
        out = out.replace("&#8216;", "'");
        out = out.replace("&#8217;", "'");
        out = out.replace("&#8230;", "");
        return out;
    };

    $scope.prev = function() {
        var newIndex = $scope.currentPostIndex - 1;
        $scope.currentPost = $scope.posts[newIndex];
        $scope.currentPostIndex = newIndex;
    };

    setCurrentPost = function(index) {
        $scope.currentPost = $scope.posts[index];
        $scope.currentPost.positionIsFixed = true;
        $scope.currentPost.position = index;
        $scope.currentPostIndex = index;
    }

    $scope.next = function(hit) {
        var hitUrl = $scope.currentPost.URL;

        // Until a post's teaser is viewed, it's neither a hit or a miss
        $scope.currentPost.isHit = (($scope.currentPost.isHit == true) || hit);
        $scope.currentPost.isMiss = !$scope.currentPost.isHit;

        addToHistory($scope.currentPost, hit);
        updateProbs();
        updatePlaylist();

        // Switch to next story
        var newIndex = $scope.currentPostIndex + 1;
        setCurrentPost(newIndex);

        if (hit) {
            //window.open(hitUrl, "winDetail");
        }
    };

    $scope.togglePlaylist = function() {
        $scope.playlistVisible = !$scope.playlistVisible;
    };

    $scope.toggleHistory = function() {
        $scope.historyVisible = !$scope.historyVisible;
    };

    /** Re-prioritise playlist based on latest probabilities */
    updatePlaylist = function() {
        angular.forEach($scope.posts, function(post, i) {
            var cat = getCategoryFromPost(post);
            var catProb = getCatProb(cat);
            post.myCatProb = catProb;
            post.genericScore = post.metrics.diff;
            post.myScore = Math.round(post.genericScore * catProb);
            
        });
        $scope.posts.sort(function(a, b) {
            if (a.positionIsFixed && b.positionIsFixed) {
                return a.position - b.position;
            }
            if (a.positionIsFixed) {
                return -1;
            }
            if (b.positionIsFixed) {
                return 1;
            }
            return b.myScore - a.myScore;
        });
    }

    getCatProb = function(cat) {
        if (cat) {
            var catProb = $scope.categoryStats[cat];
            if (catProb) {
                return catProb.softenedProb;
            }
        }
        return $scope.globalStats.softenedProb;
    }

    addToHistory = function(post) {
        // If it's in history already then update it
        var existingKey = null;
        angular.forEach($scope.postHistory, function(value, key) {
            if (value.ID == post.ID) {
                console.log("Post is already in history")
                existingKey = key;
                // XXX: Don't need to loop any more!
            }
        });

        if (existingKey === null) {
            log("Adding to history");
            $scope.postHistory.push(post);
        } else {
            log("Updating history");
            $scope.postHistory[existingKey] = post;
        }

        if ($scope.postHistory.length > $scope.MAX_HISTORY_LENGTH) {
            $scope.postHistory.shift(); // Remove first item
        }

        savePostHistory();
    };

    getCategoryFromPost = function(post) {
        var cat = null;
        angular.forEach(post.categories, function(value, key) {
            cat = value.slug;
        });
        return cat;
    };

    /* PROBABILITIES *********************************************************/

    updateProbs = function() {
        var globalStats = {hits: 0, actions: 0};
        var catStatsMap = {};

        // Go through history adding up how many hits/misses for different categories
        angular.forEach($scope.postHistory, function(post, key) {
            var cat = getCategoryFromPost(post);
            if (cat) {
                var catStats = {hits: 0, actions: 0};
                if (catStatsMap.hasOwnProperty(cat)) {
                    catStats = catStatsMap[cat];
                }
                catStats.actions++;
                globalStats.actions++;
                if (post.isHit) {
                    catStats.hits++;
                    globalStats.hits++;
                }
                catStatsMap[cat] = catStats;
            }
        });

        // Calc global stats
        if (globalStats.actions == 0) {
            globalStats.actualProb = 30;    // Arbitrary default
        } else {
            globalStats.actualProb = Math.round((globalStats.hits / globalStats.actions) * 100);
        }
        globalStats.softenedProb = Math.round(softenProb(globalStats.actualProb, globalStats.actions));
        $scope.globalStats = globalStats;

        // Calc category-specific stats
        angular.forEach(catStatsMap, function(catStats, cat) {
            var actualProb = (catStats.hits / catStats.actions) * 100;
            var softenedProb = anchorProb(actualProb, catStats.actions, $scope.globalStats.softenedProb);
            catStats.actualProb = Math.round(actualProb)
            catStats.softenedProb = Math.round(softenedProb);
        });

        // TODO: How to sort categories?
        $scope.categoryStats = catStatsMap;
    };

    /** For small samples, moves probabilities away from extremes */
    softenProb = function(prob, actionCount) {
        var ROUGHLY_GOLDEN_RATIO = 0.6; // Arbitrary
        var cushionFactor = Math.pow(ROUGHLY_GOLDEN_RATIO, actionCount);
        if ($scope.trace) {
            console.log("prob=" + prob + ", sample=" + actionCount);
        }
        if (prob <= 50) {
            return 50 - ( (50 - prob) * (1 - cushionFactor) );
        } else {
            // 5/8 = 62.5 >> 62.5 - (( 62.5 - 50) * 0.06))
            return prob - ( (prob - 50) * (cushionFactor) );
        }
    }

    /** 
     * For small samples, brings probability closer to anchor
     * @see testAnchorProb
     */
    anchorProb = function(prob, actionCount, anchorProb) {
        var ROUGHLY_GOLDEN_RATIO = 0.6; // Arbitrary
        var cushionFactor = Math.pow(ROUGHLY_GOLDEN_RATIO, actionCount);  // E.g. 0.6 ^ 1 = 0.6
        if ($scope.trace) {
            console.log("anchor=" + anchorProb + ", prob=" + prob + ", sample=" + actionCount);
        }
        if (prob <= anchorProb) {
            // E.g. 0 + ((40 - 0) * 0.6) = 24
            return prob + ((anchorProb - prob) * cushionFactor);
        } else {
            // E.g. 40 + ( (100 - 40) * (1 - 0.6) )
            return anchorProb + ( (prob - anchorProb) * (1 - cushionFactor) );
        }
    };

    /** XXX: Use Jasmine */
    $scope.testAnchorProb = function() {
        $scope.trace = true;
        var result = null;

        // Global prob = 0%

        result = anchorProb(0, 1, 0);
        console.log("expected=0, result=" + result);

        // Global prob = 40%

        result = anchorProb(0, 1, 40);
        console.log("expected=24, result=" + result);

        result = anchorProb(0, 2, 40);
        console.log("expected=14.4, result=" + result);

        result = anchorProb(40, 1, 40);
        console.log("expected=40, result=" + result);

        result = anchorProb(100, 1, 40);
        console.log("expected=64, result=" + result);

        result = anchorProb(100, 2, 40);
        console.log("expected=79, result=" + result);

        // Global prob = 60%

        result = anchorProb(0, 1, 60);
        console.log("expected=36, result=" + result);

        result = anchorProb(100, 1, 60);
        console.log("expected=76, result=" + result);

        result = anchorProb(100, 2, 60);
        console.log("expected=86, result=" + result);

        // Global prob = 100%

        result = anchorProb(100, 1, 100);
        console.log("expected=100, result=" + result);

        result = anchorProb(70, 8, 100);
        console.log("expected=71, result=" + result);

        $scope.trace = false;
    }

    /** XXX: Use Jasmine */
    $scope.testSoftenProb = function() {
        $scope.trace = true;
        var result = null;

        result = softenProb(0, 1);
        console.log("expected=30, result=" + result);

        result = softenProb(0, 2);
        console.log("expected=18, result=" + result);

        result = softenProb(25, 1);
        console.log("expected=40, result=" + result);

        result = softenProb(40, 8);
        console.log("expected=41, result=" + result);

        result = softenProb(50, 1);
        console.log("expected=50, result=" + result);

        result = softenProb(100, 1);
        console.log("expected=70, result=" + result);

        result = softenProb(100, 2);
        console.log("expected=82, result=" + result);

        result = softenProb(75, 1);
        console.log("expected=60, result=" + result);

        result = softenProb(60, 8);
        console.log("expected=59, result=" + result);

        $scope.trace = false;
    }
 
});