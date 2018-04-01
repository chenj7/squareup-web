(ns squareup-web.main-test
  (:require [clojure.test :refer :all]
            [squareup-web.main :refer :all]))

(deftest expand-url-body-test
  (testing "expands first level urls"
    (is (= (expand-url-body "http://localhost" {:url "/path"}) {:url "http://localhost/path"})))
  (testing "expands nested urls"
    (is (= (expand-url-body "http://localhost" [{:url "/path"}]) [{:url "http://localhost/path"}]))))
