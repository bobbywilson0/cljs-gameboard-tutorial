(ns ^:figwheel-always gameboard.core
  (:require [goog.dom :as dom]
            [goog.graphics :as graphics]
            [cljs.reader :as reader]))

(enable-console-print!)

;;(def ctx (.getContext (dom/getElement "canvas") "2d"))
(def canvas (dom/getElement "div"))

(def tile-size 50)
(def tile-offset (/ tile-size 2))

(def black (graphics/SolidFill. "black"))
(def red (graphics/SolidFill. "red"))

(def black-stroke (graphics/Stroke. 0.5 "black"))
(def white-stroke (graphics/Stroke. 3 "white"))

(def board-width 8)
(def board-height 8)

(def game-state
  (atom
    {:units
     [{:team :black, :x 1, :y 0},
      {:team :black, :x 3, :y 0},
      {:team :black, :x 5, :y 0},
      {:team :black, :x 7, :y 0},
      {:team :black, :x 0, :y 1},
      {:team :black, :x 2, :y 1},
      {:team :black, :x 4, :y 1},
      {:team :black, :x 6, :y 1},
      {:team :black, :x 1, :y 2},
      {:team :black, :x 3, :y 2},
      {:team :black, :x 5, :y 2},
      {:team :black, :x 7, :y 2},
      {:team :red, :x 0, :y 5},
      {:team :red, :x 2, :y 5},
      {:team :red, :x 4, :y 5},
      {:team :red, :x 6, :y 5},
      {:team :red, :x 1, :y 6},
      {:team :red, :x 3, :y 6},
      {:team :red, :x 5, :y 6},
      {:team :red, :x 7, :y 6},
      {:team :red, :x 0, :y 7},
      {:team :red, :x 2, :y 7},
      {:team :red, :x 4, :y 7},
      {:team :red, :x 6, :y 7}]}))

(def board (graphics/createGraphics (* board-width tile-size) (* board-height tile-size)))

(defn tile-color [x y]
  (if (= (even? x) (even? y))
    red
    black))

(defn unit-color [unit]
  (if (= :red (:team unit))
    red
    black))

(defn draw-tile! [x y color]
  (.drawRect board x y tile-size tile-size black-stroke color))


(defn board-position [x y]
  (map #(+ tile-offset (* tile-size %)) [x y]))

(defn draw-unit! [unit]
  (let [[x y] (board-position (:x unit) (:y unit))]
    (.drawCircle board x y 20 white-stroke (unit-color unit))))

(defn draw-board! []
  (mapv
    (fn [y]
      (mapv
        (fn [x] (draw-tile! (* tile-size x) (* tile-size y) (tile-color x y)))
        (range 0 board-width)))
    (range 0 board-height)))

(draw-board!)
(mapv draw-unit! (:units @game-state))

(.render board canvas)