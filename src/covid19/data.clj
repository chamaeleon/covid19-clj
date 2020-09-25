(ns covid19.data
  (:require [clojure.string :refer [lower-case]])
  (:require [clojure.data.csv :as csv])
  (:require [covid19.utils :as cu])
  (:gen-class))

(def fips-map (atom {}))
(def custom-fips-map (atom {}))
(def custom-fips-counter (atom 1000000))

(defn custom-fips
  "Find a good match for a missing fips code based on state and county name"
  [entry]
  (let [fips-key [(:state entry) (:county entry)]]
    (if-let [fips (@custom-fips-map fips-key)]
      (str fips)
      (let [fips (inc @custom-fips-counter)]
        (reset! custom-fips-map (assoc @custom-fips-map fips-key fips))
        (reset! custom-fips-counter fips)
        (str fips)))))

(defn fix-missing-fips [x]
  (if (= (:fips x) "")
    (assoc x :fips (custom-fips x))
    x))

(defn fix-fips
  "Some data entries have an empty string for the fips code, so replace it with a custom value"
  [data]
  (map fix-missing-fips data))

(defn make-header
  "Convert CSV string header to keywords"
  [raw]
  (map #(->> % (lower-case) (keyword)) raw))

(defn parse-csv-data
  "Parse CSV data from a string or reader, using the header content as map keywords for the data"
  [source]
  (let [data (csv/read-csv source)]
    (map zipmap
         (->> (first data) (make-header) (repeat))
         (rest data))))

(defn add-location-lookup [m entry]
  (let [name (lower-case (:name entry))
        pop (cu/string-to-long (:pop entry))]
    (-> m
        (assoc (lower-case (:abbreviation entry)) [name pop])
        (assoc (lower-case name) [name pop]))))

(defn state-lookup-table
  "Create state abbreviation to state name lookup map"
  [source]
  (reduce add-location-lookup {} (parse-csv-data source)))

(defn lowercase-location [e]
  (if (:county e)
    [(lower-case (:state e)) (lower-case (:county e))]
    [(lower-case (:state e))]))

(defn update-fips-map-for-entry [m e]
  (let [k (lowercase-location e)]
    (assoc m k (:fips e))))

(defn update-fips-map [fips-map data]
  (reduce update-fips-map-for-entry fips-map data))

(defn parse-covid-data
  "Read and parse Covid-19 data"
  [source]
  (let [data (->> (parse-csv-data source)
             (fix-fips)
             (cu/parse-numbers [:fips :cases :deaths]))]
    (reset! fips-map (update-fips-map @fips-map data))
    data))