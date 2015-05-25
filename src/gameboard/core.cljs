(ns ^:figwheel-always gameboard.core
  (:require [goog.dom :as dom]))

(enable-console-print!)

(def ctx (.getContext (dom/getElement "canvas") "2d"))
(def tile-size 50)


(defn draw-tile! [x y]
  (.setTransform ctx 1, 0, 0, 1, 0.5, 0.5)

  (.beginPath ctx)
  (.rect ctx x y tile-size tile-size)

  (set! (.-fillStyle ctx) "white")
  (.fill ctx)

  (set! (.-lineWidth ctx) 0.5)
  (set! (.-strokeStyle ctx) "black")
  (.stroke ctx))

(defn draw-board! [w h]
  (set! (.-height (dom/getElement "canvas")) (+ 1 (* h tile-size)))
  (set! (.-width (dom/getElement "canvas")) (+ 1 (* w tile-size)))

  (mapv
    (fn [y]
      (mapv
        (fn [x] (draw-tile! (* tile-size x) (* tile-size y)))
        (range 0 w)))
    (range 0 h)))

(draw-board! 3 3)