<#import "../template.ftl" as template>
<@template.base>
<main class="main-content main-content-bg mt-0">
    <div class="page-header min-vh-100">
      <span class="mask bg-gradient-dark opacity-6"></span>
      <div class="container">
        <div class="row justify-content-center">
          <div class="col-lg-6 col-md-7">
            <div class="card border-0 mb-0">
              <div class="card-body">
                <div class="text-center text-muted mb-4">
                  <h5 class="text-dark text-center mt-2 mb-3">Connexion</h5>
                </div>
                <#if error??>
                  <div class="alert alert-danger text-white" role="alert">${error}</div>
                </#if>
                <form method="post" class="text-start">
                  <div class="mb-3">
                    <input type="email" class="form-control" name="email" placeholder="Email" aria-label="Email">
                  </div>
                  <div class="mb-3">
                    <input type="password" class="form-control" name="password" placeholder="Mot de passe" aria-label="Mot de passe">
                  </div>
                  <div class="text-center">
                    <input type="submit" class="btn btn-primary w-100 my-4 mb-2" value="Connexion">
                  </div>
                  <div class="mb-2 position-relative text-center">
                    <p class="text-sm font-weight-bold mb-2 text-secondary text-border d-inline z-index-2 bg-white px-3">
                      ou
                    </p>
                  </div>
                  <div class="text-center">
                    <a href="/account/register" class="btn bg-gradient-dark w-100 mt-2 mb-4">Inscription</a>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </main>
</@template.base>
