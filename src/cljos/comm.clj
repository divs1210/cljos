(ns cljos.comm)

(comment 
"CljOS: A simple OOP system on top of Clojure"
1.Introduction
  CljOS (Clojure Object System) is a simple system that mimics
  OOP to ease transition from Java. You really shouldnt be 
  OOPing in Clojure.
  
2.Usage
  (defclass Stack
    :let  [s []]
    :this ([] @s)
    :init ([& xs]
            (swap! s concat xs))
    :push ([x]
            (swap! s conj x))
    :pop  ([]
            (let [x (last @s)]
              (swap! s (comp pop vec))
              x)))
  
  is equivalent to:
  ----------------
  
  (defn Stack [& xs]
    (let [s (atom [])]
      (swap! s concat xs)
      (fn [method & args]
        (case method
          :push (swap! s concat args)
          :pop  (let [x (last @s)]
                  (swap! s (comp pop vec))
                  x)
          :this @s))))
  
  and can be used thusly:
  ----------------------
  
  (def <s> (Stack 1 2))
  (<s> :push 3) ;=> (1 2 3)
  (<s> :pop)    ;=> 3
  (<s> :this)   ;=> [1 2]
)