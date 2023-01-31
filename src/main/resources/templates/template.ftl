<#macro base>
<!--
=========================================================
* Argon Dashboard 2 PRO - v2.0.5
=========================================================

* Product Page:  https://www.creative-tim.com/product/argon-dashboard-pro 
* Copyright 2022 Creative Tim (https://www.creative-tim.com)
* Coded by Creative Tim

=========================================================

* The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
-->
<!DOCTYPE html>
<html lang="fr">

<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <link rel="apple-touch-icon" sizes="76x76" href="/img/logo_round.png">
  <link rel="icon" type="image/png" href="/img/logo_round.png">
  <title>${title} - BDE de l'ENSISA</title>
  <!--     Fonts and icons     -->
  <link href="https://fonts.googleapis.com/css?family=Open+Sans:300,400,600,700" rel="stylesheet" />
  <!-- Nucleo Icons -->
  <link href="/css/nucleo-icons.css" rel="stylesheet" />
  <link href="/css/nucleo-svg.css" rel="stylesheet" />
  <!-- Font Awesome Icons -->
  <script src="https://kit.fontawesome.com/42d5adcbca.js" crossorigin="anonymous"></script>
  <link href="/css/nucleo-svg.css" rel="stylesheet" />
  <!-- CSS Files -->
  <link id="pagestyle" href="/css/argon-dashboard.css?v=2.0.5" rel="stylesheet" />

  <#if redirectUrl??>
  <meta http-equiv="refresh" content="0;url=${redirectUrl}">
  </#if>
</head>

<body class="g-sidenav-show bg-gray-100">
  <#nested>

  <!--   Core JS Files   -->
  <script src="/js/core/popper.min.js"></script>
  <script src="/js/core/bootstrap.min.js"></script>
  <script src="/js/plugins/perfect-scrollbar.min.js"></script>
  <script src="/js/plugins/smooth-scrollbar.min.js"></script>
  <script>
    var win = navigator.platform.indexOf('Win') > -1;
    if (win && document.querySelector('#sidenav-scrollbar')) {
      var options = {
        damping: '0.5'
      }
      Scrollbar.init(document.querySelector('#sidenav-scrollbar'), options);
    }
  </script>
  <!-- Github buttons -->
  <script async defer src="https://buttons.github.io/buttons.js"></script>
  <!-- Control Center for Soft Dashboard: parallax effects, scripts for the example pages etc -->
  <script src="/js/argon-dashboard.min.js?v=2.0.5"></script>
</body>

</html>
</#macro>

<#macro page>
  <@base>
  <div class="min-height-300 bg-primary position-absolute w-100"></div>
  <aside class="sidenav bg-white navbar navbar-vertical navbar-expand-xs border-0 border-radius-xl my-3 fixed-start ms-4 " id="sidenav-main">
    <div class="sidenav-header">
      <i class="fas fa-times p-3 cursor-pointer text-secondary opacity-5 position-absolute end-0 top-0 d-none d-xl-none" aria-hidden="true" id="iconSidenav"></i>
      <a class="navbar-brand m-0" href="/">
        <img src="/img/logo_round.png" class="navbar-brand-img h-100" alt="main_logo">
        <span class="ms-1 font-weight-bold">BDE de l'ENSISA</span>
      </a>
    </div>
    <hr class="horizontal dark mt-0">
    <div class="collapse navbar-collapse w-auto h-auto" id="sidenav-collapse-main">
      <ul class="navbar-nav">
        <#list menu as item>
        <li class="nav-item">
          <a class="nav-link" href="${item.url}">
            <span class="nav-link-text ms-1">${item.title}</span>
          </a>
          <div class="collapse show">
            <ul class="nav ms-4">
              <#list item.children as child>
              <li class="nav-item">
                <a class="nav-link" href="${child.url}">
                  <span class="sidenav-mini-icon"> ${child.short}</span>
                  <span class="sidenav-normal"> ${child.title}</span>
                </a>
              </li>
              </#list>
            </ul>
          </div>
        </li>
        </#list>
      </ul>
    </div>
    <hr class="horizontal dark">
    <div class="sidenav-footer mx-3 my-3">
      <div class="card card-plain shadow-none" id="sidenavCard">
        <div class="card-body text-center p-3 w-100">
          <div class="docs-info">
            <h6>Besoin d'aide ?</h6>
            <p class="text-xs font-weight-bold">Contactez le BDE :</p>
          </div>
        </div>
      </div>
      <a href="mailto:bde@ensisa.info" class="btn btn-dark btn-sm w-100 mb-3">bde@ensisa.info</a>
      <a href="https://www.instagram.com/bdensisa" class="btn btn-instagram btn-sm w-100 mb-3">Instagram</a>
      <a href="https://www.facebook.com/bdeensisa/" class="btn btn-facebook btn-sm w-100 mb-3">Facebook</a>
    </div>
    <hr class="horizontal dark">
    <div class="sidenav-footer mx-3 my-3">
      <div class="card card-plain shadow-none" id="sidenavCard">
        <div class="card-body text-center p-3 w-100">
          <div class="docs-info">
            <h6>Un problème avec le site ou l'app ?</h6>
          </div>
        </div>
      </div>
      <a href="mailto:dev@ensisa.info" class="btn btn-dark btn-sm w-100 mb-3">dev@ensisa.info</a>
      <a href="https://github.com/NathanFallet/BdeEnsisaWeb" class="btn btn-github btn-sm w-100 mb-3">GitHub</a>
    </div>
  </aside>
  <main class="main-content position-relative border-radius-lg ">
    <!-- Navbar -->
    <nav class="navbar navbar-main navbar-expand-lg px-0 mx-4 shadow-none border-radius-xl z-index-sticky " id="navbarBlur" data-scroll="false">
      <div class="container-fluid py-1 px-3">
        <div class="collapse navbar-collapse mt-sm-0 mt-2 me-md-0 me-sm-4" id="navbar">
          <ul class="ms-md-auto navbar-nav justify-content-end">
            <li class="nav-item d-flex align-items-center">
              <a href="/account/profile" class="nav-link text-white font-weight-bold px-0">
                <i class="fa fa-user me-sm-1"></i>
                <span class="d-sm-inline d-none">Mon compte</span>
              </a>
            </li>
            <li class="nav-item d-xl-none ps-3 d-flex align-items-center">
              <a href="javascript:;" class="nav-link text-white p-0" id="iconNavbarSidenav">
                <div class="sidenav-toggler-inner">
                  <i class="sidenav-toggler-line bg-white"></i>
                  <i class="sidenav-toggler-line bg-white"></i>
                  <i class="sidenav-toggler-line bg-white"></i>
                </div>
              </a>
            </li>
          </ul>
        </div>
      </div>
    </nav>
    <!-- End Navbar -->

    <#nested>

  </main>
  </@base>
</#macro>
