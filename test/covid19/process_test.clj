(ns covid19.process-test
  (:require [clojure.test :refer [deftest testing is]])
  (:require [covid19.process :as cp]))

(deftest fips-filter-test
  (testing "filter by fips"
    (let [input [{:foo "A", :fips 1}
                 {:foo "B", :fips 2}
                 {:foo "C", :fips 1}
                 {:foo "D", :fips 3}]
          output [{:foo "A", :fips 1}
                  {:foo "C", :fips 1}]]
      (is (= (cp/filter-by-fips 1 input) output)))))

(deftest daily-change-test
  (testing "daily change"
    (let [input [{:a 10, :b 0}, {:a 11, :b 10}, {:a 9, :b 20}, {:a 9, :b 30}, {:a 12, :b 40}]
          output-a [0 1 -2 0 3]
          output-b [0 10 10 10 10]]
      (is (= (cp/daily-change :a input) output-a ))
      (is (= (cp/daily-change :b input) output-b)))))

(deftest rolling-average-test
  (testing "rolling average"
    (let [input [1 2 3 4 5 6 7 8]
          output-1 [1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0]
          output-2 [1.0 1.5 2.5 3.5 4.5 5.5 6.5 7.5]
          output-3 [1.0 (/ 4.0 3) 2.0 3.0 4.0 5.0 6.0 7.0]]
      (is (= (cp/rolling-average 1 input) output-1))
      (is (= (cp/rolling-average 2 input) output-2))
      (is (= (cp/rolling-average 3 input) output-3)))))

(deftest gen-data-table-test
  (testing "generating data table"
    (let [input [{:date "a" :cases 1 :deaths 2} {:date "b" :cases 10 :deaths 20}]
          output [["a" 1 0 0.0 0.0 2 0 0.0 0.0]
                  ["b" 10 9 (/ 9.0 7) (/ 9.0 28) 20 18 (/ 18.0 7) (/ 18.0 28)]]]
      (is (= (cp/gen-data-table 2 input) output))
      (is (= (cp/gen-data-table 1 input) (rest output)))
      (is (= (cp/gen-data-table 0 input) output)))))