package dev.limebeck.templater.cli.render.server

fun Throwable.asHtml(): String = """
    <html>
        <head>
            <link href="https://cdn.jsdelivr.net/npm/water.css@2/out/water.css" rel="stylesheet"/>
            <title>Rendering Error</title>
        </head>
        <body>
            <h1>ERROR</h1>
            <h3>${message ?: this.toString()}</h3>
            <p>Additional error info</p>
            <pre>
                <code>
                    ${stackTraceToString().trim()}
                </code>
            </pre>
        </body>
    </html>
""".trimIndent()
