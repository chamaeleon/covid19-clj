(ns covid19.data-test
  (:require [clojure.java.io :refer [resource]])
  (:require [clojure.test :refer [deftest testing is use-fixtures]])
  (:require [covid19.data :as cd]))

(defn data-test-fixture [f]
  (reset! cd/fips-map {})
  (reset! cd/custom-fips-map {})
  (reset! cd/custom-fips-counter 1000000)
  (f))

(use-fixtures :each data-test-fixture)

(deftest fips-test
  (testing "fips code lookup"
    (let [a {:state "New York", :county "Unknown"}
          b {:state "Rhode Island", :county "Unknown"}
          c {:state "New York", :county "New York City"}
          d {:state "New York", :county "Unknown"}]
      (is (= (cd/custom-fips a) "1000001"))
      (is (= (cd/custom-fips b) "1000002"))
      (is (= (cd/custom-fips c) "1000003"))
      (is (= (cd/custom-fips d) "1000001")))))

(deftest fix-fips-test
  (testing "fips code fix"
    (let [input [{:county "New York City", :state "New York", :fips ""}
                 {:county "Unknown", :state "New York", :fips ""}
                 {:county "Nassau", :state "New York", :fips "36059"}
                 {:county "Unknown", :state "Rhode Island", :fips ""}]
          output [{:county "New York City", :state "New York", :fips "1000001"}
                  {:county "Unknown", :state "New York", :fips "1000002"}
                  {:county "Nassau", :state "New York", :fips "36059"}
                  {:county "Unknown", :state "Rhode Island", :fips "1000003"}]]
      (is (= (cd/fix-fips input) output)))))

(deftest parse-csv-test
  (testing "parse csv data"
    (let [input (slurp (resource "test/states.csv"))
          output [{:abbreviation "AL", :name "Alabama", :type "", :pop "1"}
                  {:abbreviation "AK", :name "Alaska", :type "", :pop "2"}
                  {:abbreviation "AZ", :name "Arizona", :type "", :pop "3"}]]
      (is (= (cd/parse-csv-data input) output)))))

(deftest state-lookup-test
  (testing "state lookup table"
    (let [input (slurp (resource "test/states.csv"))
          output {"al" ["alabama" 1], "alabama" ["alabama" 1]
                  "ak" ["alaska" 2], "alaska" ["alaska" 2]
                  "az" ["arizona" 3], "arizona" ["arizona" 3]}]
      (is (= (cd/state-lookup-table input) output)))))

(deftest covid-data-test
  (testing "parsing counties covid-19 data"
    (let [input (slurp (resource "test/data-counties.csv"))
          output [{:date "2020-08-01", :county "Apache", :state "Arizona", :fips 4001, :cases 3054, :deaths 135}
                  {:date "2020-08-01", :county "New York City", :state "New York", :fips 1000001, :cases 230147, :deaths 23007}
                  {:date "2020-08-01", :county "Niagara", :state "New York", :fips 36063, :cases 1444, :deaths 68}]]
      (is (= (cd/parse-covid-data input) output)))
    (let [input (slurp (resource "test/data-states.csv"))
          output [{:date "2020-08-01", :state "Arizona", :fips 4, :cases 177019, :deaths 3753}
                  {:date "2020-08-01", :state "New York", :fips 36, :cases 420477, :deaths 32390}
                  {:date "2020-08-02", :state "Arizona", :fips 4, :cases 178473, :deaths 3769}
                  {:date "2020-08-02", :state "New York", :fips 36, :cases 421008, :deaths 32401}]]
      (is (= (cd/parse-covid-data input) output)))))