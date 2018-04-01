(ns squareup-web.routes
  (:require [compojure.core :refer :all]
            [clojure.data.json :as json]
            [compojure.route :as route]
            [compojure.core :refer [defroutes routes]]
            [squareup-web.core :as c]
            [clj-time.core :as t]
            [clj-time.coerce :as tc]))

(def board (atom (c/gen-board c/standard-board-tiles)))
(def winning-board (atom (c/gen-board c/standard-board-tiles)))

(def players (atom {}))
(def winner (atom {}))

(defn parse [body]
  (json/read-str (slurp body) :key-fn keyword))

(defn res->created [result]
  {:status 201
   :headers {"Location" (:url result)}
   :body result})

(defn res->no-content []
  {:status 204})

(defn res->ok [body]
  {:status 200 :body body})

(defroutes default
           (route/resources "/")
           (route/not-found "Not Found"))

(defn curr-time-long []
  (tc/to-long (t/now)))

(defn handleWin [player]
  (let [new-winner (get (deref players) player)
        win-time (/ (- (curr-time-long) (:time new-winner)) 1000)]
    (reset! winner new-winner)
    (res->ok {:won true :myWinTime win-time})))

(defroutes app-routes
           (context "/api" []
                    (GET "/board/:player" {{player :player} :params} (res->ok {:board (c/get-rows (deref board))}))
                    (GET "/board" [] (res->ok (c/get-rows {:board (deref board)})))
                    (GET "/winningBoard" [] (res->ok {:board (c/get-winning-rows (deref winning-board))}))
                    (POST "/newGame" []
                          (do (reset! players {})
                              (res->ok (reset! board (c/gen-board c/standard-board-tiles)))))
                    (POST "/join" {body :body}
                          (do (swap! players assoc (get body "player") {:time (curr-time-long) :status :playing})
                              (res->ok {:status :playing})))
                    (GET "/winner" [] (res->ok (let [w (deref winner)]
                                                 {:winner (get w )})))
                    (POST "/checkWin" {body :body}
                          (if (c/winner? (flatten (get body "board")) (deref winning-board))
                            (handleWin (get body "player"))
                            (res->ok {:won false :myWinTime 0})))))