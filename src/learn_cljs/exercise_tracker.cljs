(ns ^:figwheel-hooks learn-cljs.exercise-tracker
  (:require
   [cljs.reader :refer [read-string]]
   [goog.dom :as gdom]
   [reagent.core :as r]
   [reagent.dom.client :as rclient]
   [reagent.ratom :as ratom]))

(defn- date-string [d]
  (let [pad-zero #(.padStart (.toString %) 2 "0")
        y (.getFullYear d)
        m (-> (.getMonth d) inc pad-zero)
        d (pad-zero (.getDate d))]
    (str y "-" m "-" d)))

(defn initial-inputs []
  {:date (date-string (js/Date.))
   :minutes "0"})

(defonce state
  (r/atom {:inputs (initial-inputs)
           :entries (or (read-string (.getItem (.-localStorage js/window) "entries")) {})}))

(defn date-input []
  (let [val (r/cursor state [:inputs :date])]
    (fn []
      [:div.input-wrapper
       [:label "Day"]
       [:input {:type "date"
                :value @val
                :on-change #(reset! val
                                    (.. % -target -value))}]])))

(defn time-input []
  (let [val (r/cursor state [:inputs :minutes])]
    (fn []
      [:div.input-wrapper
       [:label "Time (minutes)"]
       [:input {:type "number"
                :min 0
                :step 1
                :value @val
                :on-change #(reset! val (.. % -target -value))}]])))

(defn submit-button []
  [:div.actions
    [:button {:type "submit"} "Submit"]])

(defn submit-form [state]
  (let [{:keys [date minutes]} (:inputs state)]
    (-> state
        (assoc-in [:entries date] (js/parseInt minutes))
        (assoc :inputs (initial-inputs)))))

(defn form []
  [:form.input-form
   {:on-submit (fn [e]
                 (.preventDefault e)
                 (swap! state submit-form)
                 (.setItem (.-localStorage js/window) "entries" (:entries @state)))}
   [date-input]                                           ;; <3>
   [time-input]
   [submit-button]])

(def chart-width 400)
(def chart-height 200)
(def bar-spacing 2)

(defn get-points [entries]
  (let [ms-in-day 86400000
        chart-days 30
        now (js/Date.now)]
    (map (fn [i]
           (let [days-ago (- chart-days (inc i))
                 date (date-string (js/Date. (- now (* ms-in-day days-ago))))]
             (get entries date 0)))
         (range chart-days))))

(defn chart []
  (let [entries (r/cursor state [:entries])
        chart-data (ratom/make-reaction
                    #(let [points (get-points @entries)]
                       {:points points
                        :chart-max (reduce max 1 points)}))]
    (fn []
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
                   :height bar-height}])]))))

;; ...
;; Change the app function to render the chart too
(defn app []
  [:div.app
    [chart]
    [form]])

(defonce root (rclient/create-root (gdom/getElement "app")))

(defn ^:after-load start []
  (js/console.log "Start!")
  (rclient/render root [app]))

(start)

(comment

  @state

  (.. js/window -localStorage clear)
  ;;
  )
