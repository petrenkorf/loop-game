(ns quilbrowser.hero
  (:require [quil.core :as q]
            [quilbrowser.stage :as stage]))

(defn movement-x [state]
  (first (get-in state [:hero :movement])))

(defn movement-y [state]
  (last (get-in state [:hero :movement])))

(defn x [state]
  (get-in state [:hero :x]))

(defn y [state]
  (get-in state [:hero :y]))

(defn coords [state]
  (select-keys (get-in state [:hero]) [:x :y]))

(defn paused? [state]
  (get-in state [:hero :paused]))

(defn draw [state]
  (q/fill [0 200 0])
  (q/rect (:x (:hero state))
          (:y (:hero state))
          stage/tile-w
          stage/tile-h 2))

