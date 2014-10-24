(ns cljos.core)

(defn class+ [type extends vars methods]
  {:vars  (or (into (extends :vars) vars)    {})
   :fns   (or (into (extends :fns ) methods) {})
   :type  type
   :super (extends :type)})

(defmacro defclass [name extends vars methods]
  `(def ~name
     (~class+ (quote ~name)
              ~extends
              ~vars ~methods)))

(defn new+ [class-name & args]
  (let [state (atom (class-name :vars))
        fns   (class-name :fns)
        this  (fn this* [method & argv]
                (case method
                  :super  nil ;todo
                  :state  @state
                  :set   (let [[var val] argv]
                           (swap! state #(assoc % var val)))
                  :setf  (let [[var f & args] argv]
                           (this* :set var 
                             (apply (partial f (@state var)) args)))
                  :swap  (let [[f & args] argv]
                           (apply (partial swap! state f) args))
                  ;otherwise first check if it's
                  ;a method. If not, treat it as
                  ;a property.
                  (if (contains? fns method)
                    (apply (partial (fns method) this*) argv)
                    (@state method))))]
    (apply (partial (fns :init) this) args)
    this))

(def <Obj*>
  {:vars  {}
   :fns   {:init (fn [_])}
   :type  '<Obj*>
   :super nil})

(defclass <Obj> <Obj*> {} {})

;Demo usage:
;-----------
;(defclass <Stack> <Obj>
;  {:seq  []}
;  {:init (fn [this & xs]
;           (this :set :seq (vec xs)))
;   :push (fn [this x]
;           (this :setf :seq #(conj % x)))
;   :pop  (fn [this]
;           (let [x (last (this :seq))]
;             (this :setf :seq pop)
;             x))})