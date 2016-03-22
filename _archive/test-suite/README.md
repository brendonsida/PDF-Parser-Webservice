##Unit Test File Structure:

```
├── test-suite/
|   |
│   ├── tests
|   |   |
│   │   ├── <test-num>                (<test-num> increments by 1 for each test added)
│   │   │   ├── inputs
│   │   │   |   ├── coordinates       (contains comma separated coordinates only [fmt: y1,x1,y2,x2)
│   │   │   |   ├── outputtype        (contains one of the following: CSV | JSON | ? )
│   │   │   |   ├── testfile          (contains name of pdf file to test)
│   │   │   ├── outputs
│   │   │   |   ├── out.correct.csv   (contains expected output for specified file to compare against)
```

**For Example:**

```
├── test-suite/
|   |
│   ├── tests
|   |   |
│   │   ├── 1
│   │   │   ├── inputs
│   │   │   |   ├── coordinates
│   │   │   |   ├── outputtype
│   │   │   |   ├── testfile
│   │   │   ├── outputs
│   │   │   |   ├── out.correct.csv
│   │   ├── 2
...
```
