(ns covid19.process
  (:gen-class))

(defn filter-by-fips
  "Filter covid data by US fips location"
  [fips data]
  (filter #(= (:fips %) fips) data))

(defn daily-change
  "Calculate the daily change day to day for the given type of data (cases or deaths)"
  [type data]
  (conj (map (fn [[a b]] (- b a)) (partition 2 1 (map type data))) 0))

(defn rolling-average
  "Calculate the rolling average for periods of specified length"
  [days data]
  (let [data (concat (take (dec days) (repeat (first data))) data)]
    (map #(/ (float (apply + %)) (count %)) (partition days 1 data))))

(defn gen-data-table
  "Generate data table of total daily numbers, and 7-day and 28-day rolling averages"
  [days data]
  (let [dates (map :date data)
        daily-cases (daily-change :cases data)
        daily-deaths (daily-change :deaths data)
        daily-cases-7 (rolling-average 7 daily-cases)
        daily-cases-28 (rolling-average 28 daily-cases)
        daily-deaths-7 (rolling-average 7 daily-deaths)
        daily-deaths-28 (rolling-average 28 daily-deaths)
        table (mapv vector
                    dates
                    (map :cases data) daily-cases daily-cases-7 daily-cases-28
                    (map :deaths data) daily-deaths daily-deaths-7 daily-deaths-28)]
    (if (> days 0) (take-last days table) table)))