(ns squareup-web.wrapper
  (:require
    [clojure.walk :refer [prewalk prewalk-demo]]))

(defn wrap-request-logging [app]
  (fn [request]
    (let [response (app request)
          response-params (select-keys response [:status :body])
          request-params (select-keys request [:request-method :uri :content-type])
          params (merge request-params response-params)
          message (clojure.string/join " " (map #(apply format "%s=%s" %) params))]
      (println message)
      response)))

(defn cors-headers [request]
  (merge {"access-control-allow-origin" "*"}
         (cond
           (= :options (:request-method request)) {"access-control-allow-headers" "accept, content-type"
                                                   "access-control-allow-methods" "GET,HEAD,POST,DELETE,OPTIONS,PUT,PATCH"}
           :else {})))

(defn wrap-response-cors [app]
  (fn [request]
    (let [response (app request)]
      (assoc-in response [:headers] (merge (cors-headers request) (:headers response))))))

(defn wrap-response-expand-location [app]
  (fn [request]
    (let [response (app request)
          scheme (name (:scheme request))
          host (get-in request [:headers "host"])
          location (get-in response [:headers "Location"])]
      (if location
        (assoc-in response [:headers "Location"] (str scheme "://" host location))
        response))))

(defn url-node? [node]
  (and
    (vector? node)
    (= (first node) :url)))

(defn expand-url-node [prefix node]
  (let [url (second node)]
    (if (.startsWith url "/")
      {:url (str prefix url)}
      node)))

(defn expand-url-body [prefix body]
  (prewalk #(if (url-node? %) (expand-url-node prefix %) %) body))

(defn wrap-response-expand-url-body [app]
  (fn [request]
    (let [response (app request)
          scheme (name (:scheme request))
          host (get-in request [:headers "host"])
          body (:body response)]
      (assoc response :body (expand-url-body (str scheme "://" host) body)))))