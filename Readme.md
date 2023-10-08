# Templater CLI

Utility for rendering `Templater` formatted template bundles

Available CLI commands:

* `serve` - run web-server for template with live-reloading. Usage example `templater serve -t ./example/template_desc.json -d ./example/data.json` 

## Template bundle

Template bundle is plain-zip archive

`template-desc.json`
```jsonc
{
    "identifier": "my-shiny-template",
    "description": "My super shiny template",
    "entrypoint": "index.html.ftl", //relative to root of template path
    "type": "freemarker", // also possible: ftl, kote
    "tags": [""]
}
```

## Freemarker

### Available custom functions

* **getResourceUrl** - get url link for resource by it path
    ```injectedfreemarker
    <img src="${getResourceUrl("logo.png")}"/>
    ``` 
    ```injectedfreemarker
    <link rel="stylesheet" type="text/css" href="${getResourceUrl("style.css")}">
    ``` 
* **getResourceBase64** - get whole base64-encoded resource:
    ```injectedfreemarker
    <img src="${getResourceBase64("logo")}"/>
    ```

## Roadmap

* Ko-Te renderer
* PDF renderer
* Split renderer into libraries 
* `Init` command for template creation 
* Create installer
* GitHub Actions for CI
* Build with GraalVM
* Custom functions for: 
  * Image processing (scale, resize, etc)
  * Generate QR codes
