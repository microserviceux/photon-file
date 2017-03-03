(ns photon-file.core-test
  (:require [clojure.test :refer :all]
            [photon.db.file :as file]
            [photon.db-check :as check]))

(deftest db-check-test
  (let [f (java.io.File/createTempFile "test-photon-file" "pev")
        impl (file/->DBFile {:file.path (.getAbsolutePath f)})]
    (check/db-check impl)))
