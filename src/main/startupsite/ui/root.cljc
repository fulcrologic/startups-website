(ns startupsite.ui.root
  (:require
    [fulcro.client.mutations :as m :refer [defmutation]]
    [startupsite.ui.components :as components :refer [ui-placeholder ScrollTarget ui-scroll-target]]
    translations.es                                         ; preload translations by requiring their namespace. See Makefile for extraction/generation
    [fulcro.client.dom :as dom]
    #?@(:cljs [[fulcrologic.semantic-ui.factories :as s]
               [fulcrologic.semantic-ui.icons :as i]])
    [fulcro.client.primitives :as prim :refer [defsc]]
    [fulcro.client.routing :as r :refer [defrouter]]
    [startupsite.ui.screen-utils :refer [screen-type screen-ident screen-initial-state screen-query]]
    [startupsite.ui.proposals :as pro]
    [startupsite.ui.navbar :as nav :refer [NavBar]]))

(declare TopRouter)

(defsc HomePage [this {:keys [ui/main-nav-bar ui/get-started-target]}]
  {:query         (fn [] (screen-query :home-page
                           [{:ui/get-started-target (prim/get-query ScrollTarget)}
                            {[:ui/main-nav-bar '_] (prim/get-query NavBar)}]))
   :ident         (fn [] (screen-ident :home-page))
   :initial-state (fn [params] (screen-initial-state :home-page
                                 {:ui/get-started-target (prim/get-initial-state ScrollTarget {:id :get-started})}))}
  #?(:clj (dom/div nil "")
     :cljs
          (dom/div nil
            (nav/ui-nav-bar main-nav-bar :fixed-only? true)
            (s/ui-visibility #js {:onBottomPassed  #(prim/transact! this `[(nav/position-nav-bar {:fixed? true})])
                                  :onBottomVisible #(prim/transact! this `[(nav/position-nav-bar {:fixed? false})])
                                  :once            false}
              (s/ui-segment #js {:inverted  true
                                 :textAlign "center"
                                 :style     #js {:minHeight 700 :padding "1em 0em"}}
                (nav/ui-nav-bar main-nav-bar)

                (s/ui-container #js {:text true}
                  (s/ui-header #js {:as    "h1" :content "Fulcrologic" :inverted true
                                    :style #js {:fontSize "4em" :fontWeight "normal" :marginBotton 0 :marginTop "3em"}})
                  (s/ui-header #js {:as    "h2" :content "The Team That Gets You Started!" :inverted true
                                    :style #js {:fontSize "1.7em" :fontWeight "normal"}})
                  (s/ui-button #js {:primary true :size "huge"
                                    :onClick #(prim/transact! this `[(components/scroll-to {:target :get-started})])}
                    "Get Started"
                    (s/ui-icon #js {:name "right arrow"})))))

            (ui-scroll-target get-started-target)
            (s/ui-segment #js {:style #js {:padding "8em 0em"} :vertical true}
              (s/ui-grid #js {:container true :stackable true :verticalAlign "middle"}
                (s/ui-grid-row #js {}
                  (s/ui-grid-column #js {:width 16}
                    (s/ui-header #js {:as "h3" :style #js {:fontSize "2em"}} "We Help You Get Started!")
                    (dom/p #js {:style #js {:fontSize "1.33em"}}
                      "At Fulcrologic we're interested in helping companies get the
                      custom software they need at a price they can afford.")
                    (s/ui-header #js {:as "h3" :style #js {:fontSize "2em"}} "Shared Risks, Shared Rewards")
                    (dom/p #js {:style #js {:fontSize "1.33em"}} "We work with you to produce a
                    tested and well-written product on a time and materials basis. Here's the good part: you only
                    have to pay for part of the time as we go. As you start
                    to generate revenue with that product you make easy payments on the balance to eventually own the
                    software. If you fail, you owe us nothing more!"))
                  )
                (s/ui-grid-row #js {}
                  (s/ui-grid-column #js {:textAlign "center"}
                    (s/ui-button #js {:size "huge"} "Learn More!")))))

            (s/ui-segment #js {:style #js {:padding "0em"} :vertical true}
              (s/ui-grid #js {:celled "internally" :columns "equal" :stackable true}
                (s/ui-grid-row #js {:textAlign "center"}
                  (s/ui-grid-column #js {:style #js {:paddingBottom "5em" :paddingTop "5em"}}
                    (s/ui-header #js {:as "h3" :style #js {:fontSize "2em"}} "Expensive Failed Attempts?")
                    (dom/p #js {:style #js {:fontSize "1.33em"}} "Fulcrologic makes it less risky!"))
                  (s/ui-grid-column #js {:style #js {:paddingBottom "5em" :paddingTop "5em"}}
                    (s/ui-header #js {:as "h3" :style #js {:fontSize "2em"}}
                      "Custom software I can afford?")
                    (dom/div #js {:style #js {:fontSize "1.33em"}}
                      "Ideed!")))))

            (s/ui-segment #js {:style #js {:padding "8em 0em"} :vertical true}
              (s/ui-container #js {:text true}
                (s/ui-header #js {:as "h3" :style #js {:fontSize "2em"}} "How it Works")
                (dom/p #js {:style #js {:fontSize "1.33em"}}
                  "More coming soon!")
                (s/ui-button #js {:as "a" :size "large"} "Read More")
                (comment
                  (s/ui-divider #js {:as    "h4" :className "header" :horizontal true
                                     :style #js {:margin "3em 0em" :textTransform "uppercase"}}
                    (dom/a #js {:href "#"} "Case Studies"))
                  (s/ui-header #js {:as "h2" :style #js {:fontSize "2em"}} "Did we Tell You Shit?")
                  (dom/p #js {:style #js {:fondSize "2em"}} "Yeah yeah yeah...")
                  (s/ui-button #js {:as "a" :size "large"} "Tell me More"))))

            (s/ui-segment #js {:inverted true :vertical true :style #js {:padding "5em 0em"}}
              (s/ui-container nil
                (s/ui-grid #js {:divided true :inverted true :stackable true}
                  (s/ui-grid-row nil
                    (s/ui-grid-column #js {:width 3}
                      (s/ui-header #js {:inverted true :as "h4" :content "More soon..."})
                      (s/ui-list #js {:link true :inverted true}
                        (s/ui-list-item #js {:as "a"} "A")
                        (s/ui-list-item #js {:as "a"} "B")))
                    (s/ui-grid-column #js {:width 3}
                      (s/ui-header #js {:inverted true :as "h4" :content "Services"})
                      (s/ui-list #js {:link true :inverted true}
                        (s/ui-list-item #js {:as "a"} "Startup Software")
                        (s/ui-list-item #js {:as "a"} "Management Consulting")
                        (s/ui-list-item #js {:as "a"} "Apprentices")))
                    (s/ui-grid-column #js {:width 7}
                      (s/ui-header #js {:inverted true :as "h4"} "Footer Header")
                      (dom/p nil "Extra space for stuff coming soon...")))))))))


(defrouter TopRouter :top-router
  (ident [this props] (screen-ident props))
  :screen/home-page HomePage
  :screen/submit-proposal pro/SubmitProposal
  )

(def ui-top-router (prim/factory TopRouter))

(def routing-tree
  (r/routing-tree
    (r/make-route :home-page [(r/router-instruction :top-router (screen-ident :home-page))])
    (r/make-route :submit-proposal [(r/router-instruction :top-router (screen-ident :submit-proposal))])
    ))

(defsc Root [this {:keys [ui/top-router]}]
  {:initial-state (fn [params]
                    (merge routing-tree
                      {:ui/main-nav-bar (prim/get-initial-state NavBar {})}
                      {:ui/top-router (prim/get-initial-state TopRouter {})}))
   :query         [{:ui/main-nav-bar (prim/get-query NavBar)}
                   {:ui/top-router (prim/get-query TopRouter)}]}
  (ui-top-router top-router))



