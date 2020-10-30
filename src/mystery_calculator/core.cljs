(ns mystery-calculator.core
  (:require [reagent.core :as r]))

(defn- generate-cards [num-of-cards]
  (reduce
    (fn [cards number]
      (map-indexed
        #(if (bit-test number %1) (conj %2 number) %2)
        cards))
    (repeat num-of-cards (vector))
    (range 1 (Math/pow 2 num-of-cards))))

(defn- display-cards [chosen-cards cards]
  (let [toggle-choice #((if (contains? %1 %2) disj conj) %1 %2)]
    [:div.cards
      (doall
        (for [card cards]
          ^{:key card}
          [:div {:class ["card" (when (contains? @chosen-cards (first card)) "chosen")]
                 :on-click #(swap! chosen-cards toggle-choice (first card))}
            (map (fn [num] ^{:key num} [:span num]) card)]))]))

(defn- clamp [x min max]
  (if (< x min) min (if (> x max) max x)))

(defn- app []
  (let [num-of-cards (r/atom 6)
        chosen-cards (r/atom #{})]
    (fn []
      [:div
        [:p
          "Number of cards: "
          [:input {:type "number"
                   :value @num-of-cards
                   :on-change #(reset! num-of-cards (-> % .-target .-value (clamp 4 7)))}]]
        (display-cards chosen-cards (generate-cards @num-of-cards))
        (let [mystery-number (reduce + @chosen-cards)]
          (when (pos? mystery-number)
            [:p.number "Your mystery number is " [:strong mystery-number] " ✨"]))])))

(defn ^:export init []
  (reagent.dom/render [app] (.getElementById js/document "app")))
