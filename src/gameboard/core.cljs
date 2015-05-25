(ns ^:figwheel-always gameboard.core
    (:require [goog.dom :as dom]))

(enable-console-print!)

(def ctx (.getContext (dom/getElement "canvas") "2d"))

(.setTransform ctx 1, 0, 0, 1, 0.5, 0.5)

(.beginPath ctx)
(.rect ctx 0 0 50 50)

(set! (.-fillStyle ctx) "white")
(.fill ctx)

(set! (.-lineWidth ctx) 0.5)
(set! (.-strokeStyle ctx) "black")
(.stroke ctx)