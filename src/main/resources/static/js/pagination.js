// pagination
async function loadPage(url, page) {

    const urlParams = new URLSearchParams(window.location.search);

    urlParams.set('page', page);

    const response = await fetch(`${url}?${urlParams.toString()}`);
    const html = await response.text();

    document.getElementById('ajax-update-area').innerHTML = html;

}
