Hi guys, 

Last year I has been faced a bug:
Not all RESO providers have 100% valid metadata, sometimes enumerations are missing, and that is causing exceptions that cannot be easily fixed.
I am proposing in such cases to fall back to String type without rising an exception. 
Please review this simple fix and include it into the main branch. Everyone will be beneficial.
https://github.com/apache/olingo-odata4/pull/71
