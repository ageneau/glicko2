(ns glicko2.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [glicko2.core-test]))

(enable-console-print!)

(doo-tests 'glicko2.core-test)
