(ns squareup-web.core
  (:require [clojure.math.numeric-tower :refer [sqrt abs]]))

(def standard-board-tiles ["P" "P" "P" "P" "U" "U" "U" "U" "Y" "Y" "Y" "Y" "G" "G" "G" "G" "O" "O" "O" "O" "B" "B" "B" "B" " "])

(defn get-board-size [board-tiles]
  (sqrt (count board-tiles)))

(def board-size (get-board-size standard-board-tiles))


(defn gen-board [board-tiles]
  (shuffle board-tiles))

(defn get-rows [board]
  (partition board-size board))

(defn print-board-simple [board-vals]
  (let [rows (get-rows board-vals)]
    (println (clojure.string/join "\n" (map #(clojure.string/join "," %) rows)))))

(defn get-tile [board row col]
  (-> (get-rows board)
      (nth row)
      (nth col)))

(defn on-board? [row col]
  (and (>= row 0)
       (>= col 0)
       (< row (dec board-size))
       (< col (dec board-size))))

(defn swappable? [src-row src-col dest-row dest-col]
  (and
    (on-board? src-row src-col)
    (on-board? dest-row dest-col)
    (= (+ (abs (- src-row dest-row)) (abs (- src-col dest-col))) 1)))

(defn swap-tile [board src-row src-col dest-row dest-col]
  (if (swappable? src-row src-col dest-row dest-col)
    (;;TODO not needed on server side but would be fun to figure out
      )))

(defn get-win-tiles [board]
  (let [rows (get-rows board)]
    (mapcat #(take 3 (drop 1 %)) (take 3 (drop 1 rows)))))

(defn winner? [board winning-board]
  (println (get-win-tiles board))
  (println (get-win-tiles winning-board))
  (= (get-win-tiles board) (get-win-tiles winning-board)))

(defn get-winning-rows [board]
  (partition 3 (get-win-tiles board)))
