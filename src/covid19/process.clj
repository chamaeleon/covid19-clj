(ns covid19.process
  (:gen-class))

(defn filter-by-fips
  "Filter covid data by US fips location"
  [fips data]
  (filter #(= (:fips %) fips) data))

(defn make-pair-vector [type data]
  (partition 2 1 (map type data)))

(defn daily-change
  "Calculate the daily change day to day for the given type of data (cases or deaths)"
  [type data]
  (conj (map (fn [[a b]] (- b a)) (make-pair-vector type data)) 0))

(defn calculate-average [data]
  (/ (float (apply + data)) (count data)))

(defn data-subset [days data]
  (concat (take (dec days) (repeat (first data))) data))

(defn rolling-average
  "Calculate the rolling average for periods of specified length"
  [days data]
  (let [data (data-subset days data)]
    (map calculate-average (partition days 1 data))))

(defn make-statistics-vector [dates
                              total-cases daily-cases daily-cases-7 daily-cases-28
                              total-deaths daily-deaths daily-deaths-7 daily-deaths-28]
  (mapv vector dates
        total-cases daily-cases daily-cases-7 daily-cases-28
        total-deaths daily-deaths daily-deaths-7 daily-deaths-28))

(defn gen-data-table
  "Generate data table of total daily numbers, and 7-day and 28-day rolling averages"
  [days data]
  (let [dates (map :date data)
        total-cases (map :cases data)
        total-deaths (map :deaths data)
        daily-cases (daily-change :cases data)
        daily-deaths (daily-change :deaths data)
        daily-cases-7 (rolling-average 7 daily-cases)
        daily-cases-28 (rolling-average 28 daily-cases)
        daily-deaths-7 (rolling-average 7 daily-deaths)
        daily-deaths-28 (rolling-average 28 daily-deaths)
        table (make-statistics-vector dates
                                      total-cases daily-cases daily-cases-7 daily-cases-28
                                      total-deaths daily-deaths daily-deaths-7 daily-deaths-28)]
    (if (> days 0) (take-last days table) table)))