(ns quilbrowser.stage
  (:require [quil.core :as q :include-macros true]
            [quilbrowser.tiles :refer [tiles]]))

(def w 800)
(def h 800)
(def tile-w 50)
(def tile-h 50)

(defn draw-grid []
  (q/fill 0)
  (doseq [p (range 0 w tile-w)]
    (q/line p 0 p w)
    (q/line 0 p h p)))

(defn draw-path [stage-path]
  (doseq [tile stage-path]
    (q/fill (:color ((:type tile) tiles)))
    (q/rect (* tile-w (:x tile)) 
            (* tile-h (:y tile)) 
            tile-w 
            tile-h)))
