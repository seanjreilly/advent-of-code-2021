# advent-of-code-2021

Welcome to my [Advent of Code][aoc] [^aoc] solutions for 2021. If you want to see how I did any of it, please feel free. 

I did all of the coding in JetBrain IntelliJ, using [Kotlin][kotlin], so I used the JetBrains [Advent of Code Kotlin Template][template] as a starting point.
I used test-driven development because I always do (although it's a fun spare-time christmas project so I may have skimped on coverage).

Unit tests are powered by [Junit](https://junit.org/junit5/), and I also use the excellent [Kotlin Power Assert plugin](https://github.com/bnorm/kotlin-power-assert) by [Brian Norman](https://github.com/bnorm) for assertions.

I used almost[^almost] no libraries other than the kotlin stdlib. Solutions generally execute fairly quickly, although there's one or two that I wish would be faster. I didn't do any profiling.

I didn't actually finish until early Jan 2022, because I have kids and a life. Sue me ;-).

[^aoc]:
    Advent of Code is an annual event of Christmas-oriented programming challenges started December 2015.
    Every year since then, beginning on the first day of December, a programming puzzle is published every day for twenty-four days.
    You can solve the puzzle and provide an answer using the language of your choice.

[^almost]:
    On day 16 I used Apache Commons compress for an easy way to read parts of a byte from an input stream.

[aoc]: https://adventofcode.com
[kotlin]: https://kotlinlang.org
[template]: https://github.com/kotlin-hands-on/advent-of-code-kotlin-template
