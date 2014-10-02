#Robot Control Protocol

    PREPARE\n
    name: resource/path/to/script/1\n
    name: resource/path/to/script/2\n
    \n

    PREPARED\n
    content-length: N\n
    \n
    [N bytes for expected script content (aggregated)]

    START\n
    \n

    STARTED\n
    \n

    ABORT\n
    \n

    ERROR\n
    summary: [text]\n
    content-length: N\n
    [N bytes for expected script actual (aggregated)]
    \n

    FINSIHED\n
    content-length: N\n
    [N bytes for expected script actual (aggregated)]
    \n
