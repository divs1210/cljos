## Clojure Object System
------------------------
### A toy OOP system in Clojure

#### Why this heresy?
CljOS (Clojure Object System) is a simple system that mimics OOP to ease transition from Java. You really shouldn't be OOPing in Clojure. Clojure is a brilliant functional language, and it would be best to use it as such. However, I have heard that MIT undergrads used to get implementing OO Sytems on top of Scheme as homework, and I wanted to take up the challenge in Clojure.

#### What it is not
* CljOS is *not* a Clojure port of CLOS, or any other existing OO system.
* It is not half as featured as CLOS, or in fact, even a minor fraction of it, but it fits beautifully into the Clojure ecosystem.

#### Usage
Here is a thread-safe implementation of a Stack in CljOS:

```clojure
(defclass Stack 
  {:s (atom [])}
  {:init (fn [this & [xs]] 
           (swap! (=< this :s) (comp #(concat % xs) vec)))
   :push (fn [this x]
           (swap! (=< this :s) conj x))
   :pop  (fn [this]
           (let [x (last @(=< this :s))]
             (swap! (=< this :s) pop)
             x))
   :vec  (fn [this]
           (vec @(=< this :s)))})
```
which can be used in the following manner:

```clojure
(let [s (Stack)]
  (=> s :init [1 2])
  (=> s :push 3) 
  (=> s :push 4)
  (=> s :pop)
  (=> s :vec)) ;=> [1 2 3]
```

#### License
This code has been released under the EPL 1.0 license.
