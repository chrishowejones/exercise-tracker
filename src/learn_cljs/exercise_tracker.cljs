(ns learn-cljs.exercise-tracker
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [goog.dom :as gdom]))

(defn- current-date-string [d]
  (let [pad-zero #(.padStart (.toString %) 2 "0")
        y (.getFullYear d)
        m (-> (.getMonth d) inc pad-zero)
        d (pad-zero (.getDate d))]
    (str y "-" m "-" d)))

(defonce state
  (r/atom {:inputs {:date (current-date-string (js/Date.))
                    :minutes "0"}}))

(defn date-input []
  [:div.input-wrapper
    [:label "Day"]
    [:input {:type "date"
             :value (get-in @state [:inputs :date])
             :on-change #(swap! state assoc-in [:inputs :date]
                           (.. % -target -value))}]])

(defn time-input []
  [:div.input-wrapper
   [:label "Time (minutes)"]
   [:input {:type "number" :min 0 :step 1}]])

(defn submit-button []
  [:div.actions
    [:button {:type "submit"} "Submit"]])

(defn form []
  [:form.input-form
    [date-input]                                           ;; <3>
    [time-input]
    [submit-button]])

(defn- random-point []
  (js/Math.floor (* (js/Math.random) 100)))

(defonce chart-data
  (let [points (map random-point (range 30))]              ;; <1>
    (r/atom {:points points
             :chart-max (reduce max 1 points)})))

(def chart-width 400)
(def chart-height 200)
(def bar-spacing 2)

(defn chart []
  (let [{:keys [points chart-max]} @chart-data             ;; <2>
        bar-width (- (/ chart-width (count points))
                     bar-spacing)]
    [:svg.chart {:x 0 :y 0
                 :width chart-width :height chart-height}
      (for [[i point] (map-indexed vector points)          ;; <3>
            :let [x (* i (+ bar-width bar-spacing))        ;; <4>
                  pct (- 1 (/ point chart-max))
                  bar-height (- chart-height (* chart-height pct))
                  y (- chart-height bar-height)]]
        [:rect {:key i                                     ;; <5>
                :x x :y y
                :width bar-width
                :height bar-height}])]))

;; ...
;; Change the app function to render the chart too
(defn app []
  [:div.app
    [chart]
    [form]])

(rdom/render
  [app]
  (gdom/getElement "app"))                                 ;; <2>
