(ns bouncy.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(enable-console-print!)

(def initial-state
  {:color 0
   :velocity-y 0
   :size 100
   :position-x 0
   :position-y 0})

(def gravitational-pull 0.98)

(def ground-level 100)

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :hsb)
  initial-state)

(defn horizontal-center-of-sketch [] (/ (q/width) 2))

(defn vertical-center-of-sketch [] (/ (q/height) 2))

(defn draw-ellipse [x y size]
  (q/with-translation [(horizontal-center-of-sketch)
                       (vertical-center-of-sketch)]
    (q/ellipse x y size size)))

(defn shift-colour-gradually [state]
  (assoc state :color (mod (+ (:color state) 0.7) 255)))

(defn apply-vertical-velocity [state]
  (assoc state :position-y (+ (:position-y state) (:velocity-y state))))

(defn add-bounce [velocity]
  (- (* 0.80 velocity)))

(defn calculate-vertical-velocity [state]
  (let [{:keys [velocity-y
                position-y]} state]
    (assoc state :velocity-y (let [new-velocity (+ velocity-y gravitational-pull)]
                               (if (and (pos? new-velocity) (> position-y ground-level))
                                 (add-bounce new-velocity)
                                 new-velocity)))))

(defn update-state [state]
  (-> state
      shift-colour-gradually
      apply-vertical-velocity
      calculate-vertical-velocity))

(defn shift-colour-abruptly [state]
  (assoc state
         :color (+ 50 (:color state))))

(defn increase-velocity-slightly [state]
  (assoc state
         :velocity-y (* 1.3 (:velocity-y state))))

(defn click [state event]
  (-> state
      shift-colour-abruptly
      increase-velocity-slightly))

(defn colour-background-grey []
  (q/background 240))

(defn set-colour-of-sphere [state]
  (q/fill (:color state) 255 255))

(defn draw-state [state]
  (colour-background-grey)
  (set-colour-of-sphere state)
  (let [x (:position-x state)
        y (:position-y state)
        size (:size state)]
    (draw-ellipse x y size)))

(q/defsketch my-sketch
  :host "my-sketch"
  :size [500 500]
  :setup setup
  ; update-state is called on each iteration before draw-state.
  :mouse-pressed click
  :update update-state
  :draw draw-state
  :middleware [m/fun-mode])
