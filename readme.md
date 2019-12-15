
## Running the kata

run the tests, preferably in IDEA. Otherwise,
```bash

gradle test
``` 

## Notes and disclosures

* This is my "hello world" in Kotlin.

* I already took this kata in scala six days ago and went the whole ZIO 
way there https://github.com/cchepelov/sgcib-kata
    * the very existence of this file stems from feedback received as 
    part of discussing the scala edition of that kata… 
 
* In any existing project, there is already an expected error reporting 
framework and policy and I would read it and stick to it until it would 
be up for discussion again, if ever.
    * It could be exceptions (as in ye olde Java practice), custom 
sealed classes, or some monadic "railway-oriented programming" as advocated
by Arrow Kt. 
    * My Scala/FP background would push me towards using Arrow Kt's 
    Either<E, A> as the "basic brick" + explicitly monadic operations using Either.fx()      
    * I lost an entire hour beating around a binary incompatibility between the 
latest Arrow and the latest Kotlintest (the work has been done in Kotlintest but
not yet published https://github.com/kotlintest/kotlintest/pull/1081)
    * I chose to not play the Java interop card and therefore not declare
    exceptions as checked. In a real project, I expect the policy to be 
    already set and would conform to it.
    What really happened is that BDDMockito (used by Kotlintest) badgered me
    into actually starting to declare some thrown exception… allright. 
    In my original plan, I would have declared the errors anyway.
     

* In the end, gave up Arrow Kt for now, in favour of keeping Kotlintest :sadpanda:
