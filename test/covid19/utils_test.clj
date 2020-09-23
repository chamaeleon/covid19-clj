(ns covid19.utils-test
  (:require [clojure.test :refer [deftest testing is]])
  (:require [covid19.utils :as cu]))

(def state-lookup 
  {"ny" ["new york" 1]
   "mo" ["missouri" 2]})

(deftest parse-numbers-test
  (testing "string to long"
    (is (= (cu/string-to-long "1") 1))
    (is (= (cu/string-to-long "") nil))
    (is (= (cu/string-to-long "a") nil)))
  (testing "parsing numbers"
    (let [input [{:a "A1", :b "B1", :c "1", :d "2"}
                 {:a "A2", :b "B2", :c "3", :d "4"}
                 {:a "A3", :b "B3", :c "", :d "x"}]
          output [{:a "A1", :b "B1", :c 1, :d 2}
                  {:a "A2", :b "B2", :c 3, :d 4}
                  {:a "A3", :b "B3", :c nil, :d nil}]]
      (is (= (cu/parse-numbers [:c :d] input) output)))))

(deftest location-test
  (testing "making location string"
    (let [input ["Niagara, New York", "Kansas City, MO", "NY", "New York", "Narnia"]
          output [["new york" "niagara"], ["missouri" "kansas city"], ["new york"], ["new york"], ["narnia"]]]
      (is (= (map #(cu/make-location-key state-lookup %) input) output)))))