(ns photon.db.file
  (:require [cheshire.core :as json]
            [clojure.tools.logging :as log]
            [photon.db :as db])
  (:import (java.io File)))

(defn new-file [^File s] (File. s))

(defn file-name [conf]
  (:file.path conf))

(defrecord DBFile [conf]
  db/DB
  (db/driver-name [this] "file")
  (db/fetch [this stream-name order-id]
            (first (db/search this order-id)))
  (db/delete! [this id]
              (let [all (db/lazy-events this "__all__" 0)
                    filtered (remove #(= id (:local-id %)) all)]
                (db/delete-all! this)
                (dorun (map #(db/store this %) filtered))))
  (db/delete-all! [this]
                  (.delete (new-file (file-name conf)))
                  (new-file (file-name conf)))
  (db/put [this data]
          (db/delete! this (:local-id data))
          (db/store this data))
  (db/search [this id]
             (let [all (db/lazy-events this "__all__" 0)
                   filtered (filter #(= id (:local-id %)) all)]
               filtered))
  (db/store [this payload]
            (log/trace "Payload" payload)
            (let [server-timestamp (:server-timestamp payload)
                  new-payload (assoc (into {} payload) :server-timestamp
                                     (if (nil? server-timestamp)
                                       (System/currentTimeMillis)
                                       (long server-timestamp)))]
              (with-open [w (clojure.java.io/writer (file-name conf) :append true)]
                (.write w (str (json/generate-string new-payload) "\n")))))
  (db/distinct-values [this k]
                      (into #{} (map #(get % k)
                                     (db/lazy-events this "__all__" 0))))
  (db/lazy-events [this stream-name date]
                  (log/info "Retrieving events from" stream-name)
                  (try
                    (with-open [rdr (clojure.java.io/reader (file-name conf))]
                      (doall
                       (filter (fn [ev]
                                 (and
                                  (or (= "__all__" stream-name)
                                      (= :__all__ stream-name)
                                      (= stream-name (:stream-name ev)))
                                  (<= date (:server-timestamp ev))))
                               (map #(json/parse-string % true)
                                    (line-seq rdr)))))
      (catch java.io.IOException e
        '())))
  (db/lazy-events-page [this stream-name date page] []))

