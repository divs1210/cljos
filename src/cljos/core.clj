(ns cljos.core)

(defn class* [vars methods]
  {:vars    vars
   :methods (into {:init (fn [& args])}
                  methods)})

(defmacro defclass [name vars methods]
  `(defn ~name []
     (~class* ~vars ~methods)))

(defn =<
  "Get property of obj" 
  [obj property]
  (-> obj :vars property))

(defn => 
  "Call method on obj"
  [obj method & args]
  (apply (partial (-> obj :methods method) obj) args))

;Usage:
;
;(defclass Stack 
;  {:s (atom [])}
;  {:init (fn [this & [xs]] 
;           (swap! (=< this :s) concat xs))
;   :push (fn [this x]
;           (swap! (=< this :s) (comp #(conj % x) vec)))
;   :pop  (fn [this]
;           (let [x (last @(=< this :s))]
;             (swap! (=< this :s) pop)
;             x))
;   :vec  (fn [this]
;           (vec @(=< this :s)))})
;
;(let [s (Stack)]
;  (=> s :init [1 2])
;  (=> s :push 3) 
;  (=> s :push 4)
;  (=> s :pop)
;  (=> s :vec)) ;=> [1 2 3]