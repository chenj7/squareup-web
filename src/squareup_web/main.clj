(ns squareup-web.main
  (:require [compojure.core :refer [routes]]
            [compojure.handler :as handler]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.adapter.jetty :as jetty]
            [squareup-web.wrapper :refer :all]
            [squareup-web.routes :refer :all]))

(def app
  (-> (routes app-routes default)
      (handler/site)
      (wrap-response-expand-location)
      (wrap-response-expand-url-body)
      (wrap-json-body)
      (wrap-json-response)
      (wrap-response-cors)
      (wrap-request-logging)))

(defonce server (jetty/run-jetty #'app {:port (Integer. 4567) :join? false}))


