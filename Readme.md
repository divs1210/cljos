----------------------------
#Clojure Object System
----------------------------
##A toy OOP system in Clojure

#Why this heresy?
CljOS (Clojure Object System) is a simple system that mimics OOP to ease transition from Java. You really shouldnt be OOPing in Clojure. Clojure is a brilliant functional language, and it would be best to use it as such. However, I have heard that MIT undergrads used to get implementing OO Sytems on top of Scheme as homework, and I wanted to take up the challenge in Clojure.

#What it is not
* CljOS is *not* a Clojure port of CLOS, or any other existing OO system.
* It is not half as featured as CLOS, or in fact, even a minor fraction of it, but it fits beautifully into the Clojure ecosystem.

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
    :vec  ([] (vec @s)))

which can be used in the following manner:

(def <s> (Stack 1 2)) ;=> returns a Stack object
(<s> :push 3) ;=> (1 2 3)
(<s> :push 4) ;=> (1 2 3 4)
(<s> :pop)    ;=> 4
(<s> :vec)    ;=> [1 2 3]

#Note
:let and :init are reserved forms-
* :let  defines *private instance variables* that are actually atoms, and are guaranteed to be thread-safe by Clojure.
* :init defines the constructor, and is called at the time of object creation.

#How does it work?
The defclass macro transforms the previous code into this function:

  (defn Stack [& xs]
    (let [s (atom [])]
      (swap! s concat xs)
      (fn [method & args]
        (case method
          :push (swap! s concat args)
          :pop  (let [x (last @s)]
                  (swap! s (comp pop vec))
                  x)
          :vec  (vec @s)))))

which returns a closure that is basically what an Object in traditional OO is, and can be interacted with by calling methods. Observe how it is automatically thread-safe.

#Limitations
* No *this* pointer! This is a severe limitation that I plan to address soon.

#License
This code has been released under the EPL 1.0 license.
