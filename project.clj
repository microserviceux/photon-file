(defproject tranchis/photon-file "0.9.6"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [tranchis/photon-db "0.9.6"]
                 [tranchis/photon-config "0.9.7"]
                 [cheshire "5.5.0"]
                 [clj-http "1.1.2"]
                 [clj-time "0.11.0"]
                 [com.basho.riak/riak-client "2.0.1"
                  :exclusions [com.sun/tools]]])
