<html lang="en">
<head>
    <title>My shiny template</title>
</head>
<body>
    <#include "assets/included.html.ftl">
    <image src="${getResourceBase64("assets/image.png")}"/>
    <image src="${getResourceUrl("assets/image.png")}"/>
</body>
</html>
