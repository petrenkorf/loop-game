(ns quilbrowser.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]
            [quilbrowser.hero :as hero]
            [quilbrowser.stage :as stage]))

(declare quilbrowser)

(def path [{:x 4 :y 4  :type :road}
           {:x 4 :y 5  :type :road}
           {:x 4 :y 6  :type :road}
           {:x 4 :y 7  :type :road}
           {:x 4 :y 8  :type :road}
           {:x 4 :y 9  :type :road}
           {:x 5 :y 9  :type :road}
           {:x 6 :y 9  :type :road}
           {:x 7 :y 9  :type :road}
           {:x 8 :y 9  :type :road}
           {:x 9 :y 9  :type :road}
           {:x 10 :y 9 :type :road}
           {:x 11 :y 9 :type :road}
           {:x 11 :y 8 :type :road}
           {:x 11 :y 7 :type :road}
           {:x 11 :y 6 :type :road}
           {:x 11 :y 5 :type :road}
           {:x 10 :y 5 :type :road}
           {:x 9 :y 5  :type :road}
           {:x 8 :y 5  :type :road}
           {:x 8 :y 4  :type :road}
           {:x 7 :y 4  :type :campfire}
           {:x 6 :y 4  :type :road}
           {:x 5 :y 4  :type :road}])

(defn stage-path [] path)

;stage
(defn get-firecamp-position [path]
  (->> path 
       (filter (comp #{:campfire} :type)) 
       first))

;stage
(defn get-next-tile [path cur-tile]
  (let [cur-idx (.indexOf path cur-tile)]
    (nth (cycle path) (inc cur-idx))))

(defn tile->pixel [tile]
  {:x (* stage/tile-w (:x tile)) 
   :y (* stage/tile-h (:y tile))})

(defn setup []
  (q/frame-rate 60)
  (let [initial-tile (select-keys (get-firecamp-position path) [:x :y])
        next-tile    (get-next-tile (map #(select-keys % [:x :y]) path) initial-tile)
        x-tile       (:x initial-tile)
        y-tile       (:y initial-tile)]
    {:hero {:x (* stage/tile-w x-tile) 
            :y (* stage/tile-h y-tile)
            :movement [0 0]
            :current-tile initial-tile
            :paused false}
     :next-tile next-tile
     :next-tile-position (tile->pixel next-tile)}))

(defn update-hero-direction [state]
  (assoc-in state 
            [:hero :movement] 
            [(compare (get-in state [:next-tile-position :x])
                      (hero/x state))
             (compare (get-in state [:next-tile-position :y])
                      (hero/y state))]))

(defn update-hero-position [state]
  (-> state
    (update-in [:hero :x] + (hero/movement-x state))
    (update-in [:hero :y] + (hero/movement-y state))))

(defn update-next-tile? [state]
  (let [next-tile-position (select-keys (:next-tile-position state) [:x :y])]
    (= (hero/coords state) next-tile-position)))

(defn update-hero-next-tile [state]
  (if (update-next-tile? state)
    (let [new-next-tile (get-next-tile (map #(select-keys % [:x :y]) path)
                                       (get-in state [:next-tile]))
          next-tile-position (tile->pixel new-next-tile)]
      (-> state
        (assoc-in [:next-tile-position] next-tile-position)
        (assoc-in [:next-tile] new-next-tile)))
    state))

(defn update-hero [state]
  (if (hero/paused? state)
    state
    (-> state
      update-hero-next-tile
      update-hero-direction
      update-hero-position
      )))

(defn update-state [state]
  (-> state
    update-hero))

(defn draw-state [state]
  (q/background 80)
  (stage/draw-grid)
  (stage/draw-path (stage-path))
  (hero/draw state))

(defn toggle-pause [state]
  (update-in state [:hero :paused] not))

(defn handle-key-press [state key-pressed]
  (cond
    (= :space (:key key-pressed)) (toggle-pause state)
    :else state))

(defn ^:export run-sketch []
  (q/defsketch quilbrowser
    :host "quilbrowser"
    :size [stage/w stage/h]
    :setup setup
    :update update-state
    :key-pressed handle-key-press
    :draw draw-state
    :middleware [m/fun-mode]))

; uncomment this line to reset the sketch:
(run-sketch)
