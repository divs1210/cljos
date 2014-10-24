(ns cljos.core)

(defn class+ [type extends vars methods]
  {:vars  (or (into (extends :vars) vars)    {})
   :fns   (or (into (extends :fns ) methods) {})
   :type  (type    :type)
   :super (extends :type)})

(defmacro defclass [name extends vars methods]
  `(def ~name
     (~class+ ~name
              ~extends
              ~vars ~methods)))

(defn new+ [class-name & args]
  (let [state (atom (class-name :vars))
        fns   (class-name :fns)
        this  (fn this* [method & argv]
                (case method
                  :super nil ;todo
                  :state @state
                  :set   (let [[var val] argv]
                           (swap! state #(assoc % var val)))
                  :swap  (let [[f & args] argv]
                           (apply (partial swap! state f) args))
                  ;otherwise first check if it's
                  ;a method. If not, treat it as
                  ;a property.
                  (if (contains? fns method)
                    (apply (partial (fns method) this*) argv)
                    (get @state (first argv) nil))))]
    (apply (partial (fns :init) this) args)
    this))

(def <Obj*>
  {:vars  {}
   :fns   {:init (fn [_])}
   :type  '<Obj*>
   :super nil})

(defclass <Obj> <Obj*> {} {})

(defclass <Stack> <Obj>
  {:seq  []}
  {:init (fn [this & xs]
           (this :swap #(assoc % :seq) (fn [_] (vec xs))))
   :push (fn [this x]
           (this :swap #(assoc % :seq conj x) x))
   :pop  (fn [this]
           (let [x (last (this :seq))]
             (this :swap #(assoc % :seq (comp vec butlast)))))})