# Drawing a game board in HTML Canvas with ClojureScript

## Intro

The goal of this tutorial is to get some practice using ClojureScript with the HTML Canvas element, including JavaScript interoperability. The byproduct is that we build something that might be useful. We have access to the browser, and I find creating something visual to aid in the learning process I am going to walk through how to build a gameboard. At the end of this tutorial you will be able to use ClojureScript with the HTML Canvas element to create an 8x8 Checkers board. 

This walkthrough is targeted primarily at people that are beginners to advanced-beginners with ClojureScript. It does assumes a very basic knowledge of Clojure/ClojureScript, even if you are very new you should be able to get through it by following along and looking things up as we go. I’ve found this [post](http://www.spacjer.com/blog/2014/09/12/clojurescript-javascript-interop/) to be especially helpful as a JS interop reference.

I chose to use [leiningen](http://leiningen.org/) for this tutorial although it isn't required (see [https://github.com/clojure/clojurescript/wiki/Quick-Start](https://github.com/clojure/clojurescript/wiki/Quick-Start)). I also am using [lein-figwheel](https://github.com/bhauman/lein-figwheel), it automatically reloads our ClojureScript code in the browser. It also gives you a REPL to fiddle with your live application as it's running.

## Setup the application

Let’s start by creating a new ClojureScript project with leinegen. If you don’t have leiningen, go [here](http://leiningen.org/) and follow the installation instructions.

1. `lein new figwheel gameboard`
2. `cd gameboard`
3. Open up the `gameboard` project in your editor.
4. Run `lein figwheel` from your terminal. 

    If everything went well the last three lines of your terminal 	should look like this:

    ```
    Prompt will show when fig wheel connects to your application
    To quit, type: :cljs/quit
    cljs.user=> 
    ```
	
    If this didn’t work see the [troubleshooting](#troubleshooting) section.

5. In your text editor open `./resources/public/templates/index.html`.
6. Immediately after the `<body>` tag add `<canvas id=“canvas”></canvas>` and remove the `<div id=“app”>` and it’s contents.
7. In your text editor open `./src/gameboard/core.cljs` and remove all of the code below `(enable-console-print!)`. 
8. Open up a browser and navigate to [http://localhost:3449](http://localhost:3449).

	The page will be blank, but by opening the inspector or viewing the source you should be able to see our `<canvas id=“canvas”></canvas>` element in the page source. Now our app and workflow is pretty well set up so that we can start drawing on the canvas.

### [Troubleshooting](#troubleshooting)

If this didn’t work for you go through these steps:

1. Make sure you have at least version 1.7 of java. Check this by running `java -version`.
2. Make sure leiningen is setup correctly. Check this by running `lein`.
3. Remove and go through the steps again. Remove the *gameboard* directory, and start from step one on the instructions above.

[code at this point](https://github.com/bobbywilson0/gameboard/tree/01_setup_the_application)

## Drawing Basics

1. In your text editor, open `./src/gameboard/core.cljs`.
2. At the top of your file in the namespace `(ns ...)` section replace `(:require)` with `(:require [goog.dom :as dom])`. This will give us access to the dom methods in the Google Closure library which you have access to in to any ClojureScript project.

2. Add the following line to the bottom of your file:

    ```clojure
    (def ctx (.getContext (dom/getElement "canvas") "2d"))
    ```

    We are defining a Var `ctx` that stores a reference to the canvas context. We access the canvas context by using some ClojureScript interop. 

    ```clojure
    (.getContext (dom/getElement "canvas") "2d")
    ```

    The line above uses a bit of syntactic sugar to save us a set of parens, and I prefer the look of it over the long-hand version below.

    ```clojure
    (. (dom/getElement "canvas") (getContext "2d"))
    ```

    Using `(.function Object arg)` is how you call methods on objects when they have been already defined in JavaScript somewhere.  If you are familiar with JavaScript it might be useful to see the rough equivalent in JavaScript.

    ```javascript
    var ctx = goog.dom.getElement('canvas').getContext("2d");
    ```
    
    All of the drawing methods use the canvas context and not the canvas element directly. This is because there are multiple contexts `2d`, `webgl`, and `webgl2`. We are going to be using the `2d` context throughout this tutorial since we are only drawing 2d shapes. We are fetching the `2d` context via `getContext` for our canvas element, and storing it in `ctx` for easy access later.

4. Let's draw a simple box.

    ```clojure
   (.beginPath ctx)
   (.rect ctx 0 0 50 50)
   
   (set! (.-fillStyle ctx) "white")
   (.fill ctx)
   
   (set! (.-lineWidth ctx) 0.5)
   (set! (.-strokeStyle ctx) "black")
   (.stroke ctx)
    ```
    
    It should look like the image below:
    ![box](http://i.imgur.com/HjFBpRb.png)

    There's four concepts to get down in this bit of code.
    
    ```clojure
    (.beginPath ctx)
    ```
    
    [beginPath](https://developer.mozilla.org/en-US/docs/Web/API/CanvasRenderingContext2D/beginPath) tells the renderer that we aren't continuing on any previous path, we want to start a new one with the context we give it.
    
    ```clojure
    (.rect ctx 0 0 50 50)
    ```
    
    [rect](https://developer.mozilla.org/en-US/docs/Web/API/CanvasRenderingContext2D/rect) takes four args x, y, width, and height. What we have at this point is the invisible skeleton of our rectangle. As in, there is nothing to see yet.
    
    ```clojure
    (set! (.-fillStyle ctx) "white")
    (.fill ctx)
    ```
    
    We are now seeing a new function `set!` and the JavaScript interop method call, but instead of the method being preceded with a `.` we see `.-`. This means that we are not calling `fillStyle` as a method, but we are accessing `fillStyle` as a property. This might now start to make sense what we are doing with `set!`. We are getting the `fillStyle` property with `.-fillStyle` and then wrapping it in a `set!` to update the value of the `fillStyle` property. If you are new to Clojure/ClojureScript the `!` at the end of the method means that we are changing state. 
    
    The equivalent in JavaScript would look like this:
    
    ```JavaScript
    ctx.fillStyle = "white";
    ```
    
    Setting the `fillStyle` doesn't mean that the fill will be rendered, we are explicitly telling it to draw the fill with the `fill` method on the context.
    
    ```clojure
    (set! (.-lineWidth ctx) 0.5)
	(set! (.-strokeStyle ctx) "black")
	(.stroke ctx)
    ```
    
    The previous section of code isn't anything new or particularly interesting. We just covered setting properties. As is done here with the `.lineWidth` and the `.strokeStyle`. This code is another example of the set and render style that we saw earlier, where all of the settings are defined, and then the `stroke` method is called to actually render the stroke.
    
    Now that we have a single box, we are on our way to making a full game board. It will just be a square tile grid, for games like Checkers, Chess, and Stratego. 
    
    There is one thing that bugs me about the square that we have drawn. On the right side, and bottom side of the square it appears to be a fuzzy line. I tried fixing it with setting a narrow `lineWidth` as we did above, but that didn't seem to completely fix it. I did find a solution for it, but it isn't a very satisfying one.
    
    Add this line above the `(.beginPath ctx)` line:

    ```clojure
    (.setTransform ctx 1, 0, 0, 1, 0.5, 0.5)
    ```
    
    ![](http://i.imgur.com/3MGjkqt.png)
    
    Notice that fuzziness is gone? I found the fix [here](http://stackoverflow.com/questions/8696631/canvas-drawings-are-blurry), and yes, it does feel like a hack, but there doesn't seem to be a better way around it. The `setTransform` method takes six parameters, we care about two of them, the last two. As you can see we add 0.5px offset to both the x and y axis.
    
[code at this point](https://github.com/bobbywilson0/gameboard/tree/02_drawing_basics)
    
## Making a Board

To make a board all we really need to do is call the code that we used to draw the first square for whatever size board we want. There isn't that much work in refactoring our existing code into something that is reusable in the way we described.

1. Let's wrap our code up in a function called `draw-tile!`. It should take an x and y value for where our tile should start being rendered. I also want to pull the size of the tile out into a Var so we can reuse it. 

	Our code should look something like this:

	```clojure
    (def ctx (.getContext (dom/getElement "canvas") "2d"))
	(def tile-size 50)

	(.setTransform ctx 1, 0, 0, 1, 0.5, 0.5)

	(defn draw-tile! [x y]
  	  (.beginPath ctx)
	  (.rect ctx x y tile-size tile-size)

	  (set! (.-fillStyle ctx) "white")
	  (.fill ctx)

	  (set! (.-lineWidth ctx) 0.5)
	  (set! (.-strokeStyle ctx) "black")
	  (.stroke ctx))
      
    (draw-tile! 0 0)
    ```
    
    The refactor we just did should result in the exact same square we had on the screen previously.
    
2. Now we need to write a function that draws a bunch of tiles to make up our board.

    ```clojure
    (defn draw-board! [w h]
	  (mapv
    	(fn [y]
	      (mapv
    	    (fn [x] (draw-tile! (* tile-size x) (* tile-size y)))
	        (range 0 w)))
   	 (range 0 h)))
     ```
     
     This function takes a width and a height argument for how many tiles wide and tall you want your game board. In the function body, it is two `mapv` functions, `mapv` applies the function to each item in the collection, and returns a vector of the result in a non-lazy fashion. We use two ranges to give us all of the integer values from 0 to the size of our width or height.
     
3. Using our new `draw-board!` function we can draw a game board of any size. Draw a 3x3 board, and you should notice one issue we need to take care of.

	![](http://i.imgur.com/lYnOJSb.png)
    
    We have a cutoff board because of the limit on our canvas size. The easy way to fix this (and for now is fine) is to just set a height and width attribute on the canvas element. You may try to do this in your CSS file but it will stretch your canvas. A more robust way is to dynamically set the canvas size based on the width and height of your board.
    
    ```clojure
	(defn draw-board! [w h]
  	  (set! (.-height (dom/getElement "canvas")) (+ 1 (* h tile-size)))
	  (set! (.-width (dom/getElement "canvas")) (+ 1 (* w tile-size)))

	  (mapv
    	(fn [y]
	      (mapv
    	    (fn [x] (draw-tile! (* tile-size x) (* tile-size y)))
        	(range 0 w)))
	    (range 0 h)))
    ```

	The two lines we added at the top of the method set the width and height properties based on the size of the gameboard. You may also be wondering why we are adding 1 to our width and height, unfortunately this is because of our offset we set to get the lines to render sharp.

	I also noticed that there was some fuzziness when the grid of tiles was rendered. To fix this I moved the `setTransform` function inside of the `draw-board!` function.

    Your board should look like this:
    
    ![](http://i.imgur.com/MxzlhVk.png)
    
    [code at this point](https://github.com/bobbywilson0/gameboard/tree/03_making_a_board)

## Making a Checkers board

The standard Checkers board is an 8x8 grid of tiles. The tiles are colored cycling between two colors.

1.  Let's start by making an 8x8 grid with our existing `draw-board!` function.

    ```clojure
    (draw-board! 8 8)
    ```
    
2. To implement the checker pattern, the first thought that I have is to use the `x` and `y` values of our `mapv` functions to figure out if the tile should be red or black. Knowing that we will have those values, we can right a function that takes in an `x` and `y` and will tell us if that tile should be red or black.

	```clojure
	(defn tile-color [x y]
  	  (if (= (even? x) (even? y))
        "red"
        "black"))
    ```
    
    This function takes an `x` and `y` value and checks to see if each value is even, then compares the result of whether the even-ness of `x` and `y` are the same. `[0,0]` both would be true, return "red", `[0, 1]` `x` would be true and `y` would be false, return "black".
    
3. Now we need to change our `draw-tile!` function so that it takes an additional argument `color`, and when we draw the rectangle we need to pass in that color.

    ```clojure
    (defn draw-tile! [x y color]
      (.setTransform ctx 1, 0, 0, 1, 0.5, 0.5)

      (.beginPath ctx)
      (.rect ctx x y tile-size tile-size)

      (set! (.-fillStyle ctx) color)
      (.fill ctx)

      (set! (.-lineWidth ctx) 0.5)
      (set! (.-strokeStyle ctx) "black")
      (.stroke ctx))
    ```
  
    We only needed to change two lines, but I included the whole function to give context. We added the argument, and you can see we reference that argument when we set the `fillStyle`.
    
4. In the `draw-board!` function we need to use the value of `x` and `y` when we are calling our `draw-tile!` function. Since `draw-tile!` takes a third argument we can use the `tile-color` function. `tile-color` takes in the `x` and `y` values of the current iteration and will return "red" or "black" which will get passed on to the `draw-tile!` function and become the fill color for our tile.

    ```clojure
    (defn draw-board! [w h]
  	  (set! (.-height (dom/getElement "canvas")) (+ 1 (* h tile-size)))
	  (set! (.-width (dom/getElement "canvas")) (+ 1 (* w tile-size)))

	  (mapv
    	(fn [y]
      	  (mapv
     	   (fn [x] (draw-tile! (* tile-size x) (* tile-size y) (tile-color x y)))
    	   	 (range 0 w)))
    	   (range 0 h)))
    ```
    
5. The last thing that we need to do is change the size of our board from a 3x3 to an 8x8.

    ```clojure
    (draw-board! 8 8)
    ```

![](http://i.imgur.com/IWMagBz.png)

[code at this point](https://github.com/bobbywilson0/gameboard/tree/04_making_a_checkers_board)

## Wrap Up

In this tutorial we used ClojureScript with JavaScript interop to draw a game board with the HTML Canvas element. I am hoping that this will be the first part in a series on making an interactive board game with ClojureScript. I am only slightly ahead of this tutorial working on a personal project to do just that. Hopefully we have met the goal which was to get some practice using ClojureScript, Canvas, and doing some interop with JavaScript.

Check out the finished code here: [https://github.com/bobbywilson0/gameboard](https://github.com/bobbywilson0/gameboard)

## Resources

- [http://cljs.info/cheatsheet/](http://cljs.info/cheatsheet/)
- [http://himera.herokuapp.com/index.html](http://himera.herokuapp.com/index.html)
- [http://www.spacjer.com/blog/2014/09/12/clojurescript-javascript-interop/](http://www.spacjer.com/blog/2014/09/12/clojurescript-javascript-interop/)
- [https://developer.mozilla.org/en-US/docs/Web/API/Canvas_API](https://developer.mozilla.org/en-US/docs/Web/API/Canvas_API)
