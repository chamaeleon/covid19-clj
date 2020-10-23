(ns covid19.core
  (:require [clojure.string :as s])
  (:require [clojure.java.io :refer [reader resource]])
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:require [covid19.data :as cd])
  (:require [covid19.process :as cp])
  (:require [covid19.utils :as cu])
  (:gen-class))

(def cli-options
  [["-d" "--days <n>" "Limit output to n most recent days"
    :default 28
    :parse-fn #(Long/parseLong %)]
   ["-s" "--search <string>" "Find fips entries matching the provided string"]
   ["-h" "--help" "Display help message"]])

(defonce state-lookup (atom {}))
(defonce data-states (atom []))
(defonce data-counties (atom []))

(defn initialize
  "Read Covid-19 data from a directory containing the NY Times Github data,
   and initialize a state abbreviation lookup table for searching data"
  [directory]
  (let [data-states-file (str directory "/us-states.csv")
        data-counties-file (str directory "/us-counties.csv")]
    (with-open [rdr (reader (resource "states.csv"))]
      (reset! state-lookup (doall (cd/state-lookup-table rdr))))
    (with-open [rdr (reader data-states-file)]
      (reset! data-states (doall (cd/parse-covid-data rdr))))
    (with-open [rdr (reader data-counties-file)]
      (reset! data-counties (doall (cd/parse-covid-data rdr)))))
  nil)

(defn print-table [table]
  (println "Date           Cases     Daily    7d-avg   28d-avg    Deaths     Daily    7d-avg   28d-avg")
  (doseq [entry table]
    (println (apply format "%s%10d%10d%10.1f%10.1f%10d%10d%10.1f%10.1f" entry))))

(defn loc-str
  "Generate location string for FIPS search"
  [entry]
  (s/join ", " (filter identity [(:county entry) (:state entry)])))

(defn location-and-fips-vector
  "Return sorted unique sequence of locations with FIPS code"
  [data]
  (sort (into #{} (map #(vector (loc-str %) (:fips %)) data))))

(defn print-fips
  "Display all FIPS locations and FIPS code that match the search string"
  [data search]
  (let [pat (re-pattern (str "(?i)" search))
        mapping (location-and-fips-vector data)]
    (doseq [[loc fips] mapping]
      (when (re-find pat loc)
        (println (str loc " -> FIPS " fips))))))

(defn search
  "Search states and county data for FIPS codes"
  [options]
  (print-fips @data-states (:search options))
  (print-fips @data-counties (:search options)))

(defn generate-table [fips days]
  (let [data-set (if (< fips 100) @data-states @data-counties)
        table (cp/gen-data-table days (cp/filter-by-fips fips data-set))]
    (print-table table)))

(defn generate-statistics [arguments options]
  (let [location (second arguments)
        days (:days options)]
    (if-let [fips (or (cu/string-to-long location)
                      (@cd/fips-map (cu/make-location-key @state-lookup location)))]
      (generate-table fips days)
      (println (str "Invalid location '" (second arguments) "'")))))

(defn -main [& args]
  (let [{:keys [options arguments summary]} (parse-opts args cli-options)]
    (if
     (:help options) (println summary)
     (do
       (initialize (first arguments))
       (cond
         (:search options) (search options)
         :else (generate-statistics arguments options))))))