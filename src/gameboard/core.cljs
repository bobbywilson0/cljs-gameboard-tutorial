(ns ^:figwheel-always gameboard.core
  (:require [goog.dom :as dom]
            [goog.graphics :as graphics]
            [goog.events :as events]))

(enable-console-print!)

(def tile-size 50)
(def tile-offset (/ tile-size 2))

(def piece-radius 20)

(def black (graphics/SolidFill. "black"))
(def red (graphics/SolidFill. "red"))
(def blue (graphics/SolidFill. "blue"))


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

(def board (graphics/CanvasGraphics. (* board-width tile-size) (* board-height tile-size)))
(def board-dom (.createDom board))


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

(defn board-to-client [x y]
  (map #(+ (* tile-size %) tile-offset) [x y]))

(defn client-to-board [x y]
  (map #(- (.ceil js/Math (/ % tile-size)) 1) [x y]))

(defn draw-unit! [unit]
  (let [[x y] (board-to-client (:x unit) (:y unit))]
    (.drawEllipse board x y piece-radius piece-radius white-stroke (unit-color unit))))

(defn draw-units! []
  (mapv draw-unit! (:units @game-state)))

(defn draw-board! []
  (mapv
    (fn [y]
      (mapv
        (fn [x] (draw-tile! (* tile-size x) (* tile-size y) (tile-color x y)))
        (range 0 board-width)))
    (range 0 board-height)))

(draw-board!)
(draw-units!)

(.render board)
(events/listen
  (aget (.getElementsByTagName js/document "canvas") 0)
  events/EventType.MOUSEDOWN
  (fn [e]
    ;(.render board)
    (.drawRect board (.-offsetX e) (.-offsetY e) 10 10 white-stroke blue)))

;(println (client-to-board (.-offsetX e) (.-offsetY e)))