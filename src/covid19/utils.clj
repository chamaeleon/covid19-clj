(ns covid19.utils
  (:require [clojure.string :as str])
  (:gen-class))

(defn string-to-long
  "Convert a string into a long, returning 0 if it fails"
  [s]
  (try (Long/parseLong s)
       (catch Exception _ nil)))

(defn parse-numbers
  "Convert data containing numbers from string to longs"
  [columns data]
  (map #(reduce (fn [entry col] (assoc entry col (string-to-long (entry col))))
                % columns)
       data))

(defn make-location-key
  [state-lookup loc]
  (let [[potential_county potential_state] (map str/trim (str/split (str/lower-case loc) #","))]
    (if potential_state
      ;; Both county and state specified
      (if-let [[long-state-name _] (state-lookup potential_state)]
        [long-state-name potential_county]
        [potential_state potential_county])
      ;; Only state specified
      (if-let [[long-state-name _] (->> loc (str/lower-case) (str/trim) (state-lookup))]
        [long-state-name]
        [(->> loc (str/lower-case) (str/trim))]))))