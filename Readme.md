##Clojure Object System
----------------------------
#A toy OOP system in Clojure
----------------------------
#Why this heresy?
CljOS (Clojure Object System) is a simple system that mimics OOP to ease transition from Java. You really shouldnt be OOPing in Clojure. Clojure is a brilliant functional language, and it would be best to use it as such. However, I have heard that MIT undergrads used to get implementing OO Sytems on top of Scheme as homework, and I wanted to take up the challenge in Clojure.

#What it is not
1. CljOS is not production-quality. It is just a toy that I'm playing around with.
2. CljOS is *not* a Clojure port of CLOS, or any other existing OO system.
3. No meta-classes.

#Usage
Here is a thread-safe implementation of a Stack in CljOS:

(defclass Stack
  :let  [s []]
  :init ([& xs]
          (swap! s concat xs))
  :push ([x]
          (swap! s conj x))
  :pop  ([]
          (let [x (last @s)]
            (swap! s (comp pop vec))
            x))
  :this ([] @s))

which can be used in the following manner:

(def <s> (Stack 1 2)) ;=> returns a Stack object
(<s> :push 3) ;=> (1 2 3)
(<s> :push 4) ;=> (1 2 3 4)
(<s> :pop)    ;=> 4
(<s> :this)   ;=> [1 2 3]

#Note
1. The :let form defines *private instance variables* that are actually atoms, and are guaranteed to be thread-safe by Clojure.
2. :init defines the constructor, and is called at the time of object creation.

#How does it work?
The defclass macro converts the above class into this function:

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
