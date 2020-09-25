(ns covid19.utils
  (:require [clojure.string :as str])
  (:gen-class))

(defn string-to-long
  "Convert a string into a long, returning 0 if it fails"
  [s]
  (try (Long/parseLong s)
       (catch Exception _ nil)))

(defn col-number-conversion [entry col]
  (assoc entry col (string-to-long (entry col))))

(defn convert-number-columns [columns entry]
  (reduce col-number-conversion entry columns))

(defn parse-numbers
  "Convert data containing numbers from string to longs"
  [columns data]
  (map #(convert-number-columns columns %) data))

(defn make-location-key-county-state [state-lookup potential-county potential-state]
  (if-let [[long-state-name _] (state-lookup potential-state)]
    [long-state-name potential-county]
    [potential-state potential-county]))

(defn make-location-key-state [state-lookup loc]
  (if-let [[long-state-name _] (->> loc (str/lower-case) (str/trim) (state-lookup))]
    [long-state-name]
    [(->> loc (str/lower-case) (str/trim))]))

(defn make-location-key
  [state-lookup loc]
  (let [[potential-county potential-state] (map str/trim (str/split (str/lower-case loc) #","))]
    (if potential-state
      (make-location-key-county-state state-lookup potential-county potential-state)
      (make-location-key-state state-lookup loc))))