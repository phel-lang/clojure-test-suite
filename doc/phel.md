# Running the Phel tests

## Pre-requisities

- PHP 8.4+ & [Composer](https://getcomposer.org/doc/00-intro.md#installation-linux-unix-macos)

Install Phel:
```
composer install
```

See also [Getting Started guide](https://phel-lang.org/documentation/getting-started/).

## Running the test suite

Run full suite:
```
./vendor/bin/phel test
```
Run specific test:
```
./vendor/bin/phel test test/clojure/core_test/abs.cljc
```

If test runner crashes before producing a report, run the tests with more verbosity using `--testdox` or `-v` flag which may help tracking down the specific test where failure is coming from.

See also Phel testing docs on [running tests](https://phel-lang.org/documentation/testing/#running-tests).

## Updating Phel version

Specific [Phel repository](https://github.com/phel-lang/phel-lang/) commit hash is pinned in `composer.json`:
```javascript
{
    "require": {
        "phel-lang/phel-lang": "dev-main#73920b1"
    }
}
```

It can be changed manually and updated to with `composer update`.

Alternatively `composer require` changes the pinned version in `composer.json` and updates packages automatically:

```
composer require "phel-lang/phel-lang:dev-main#73920b1"
```
