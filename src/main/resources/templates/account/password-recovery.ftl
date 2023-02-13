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
                  <h5 class="text-dark text-center mt-2 mb-3">Mot de passe oublié</h5>
                </div>
                <#if error??>
                  <div class="alert alert-danger text-white" role="alert">${error}</div>
                </#if>
                <#if success??>
                  <div class="alert alert-success" role="alert">${success}</div>
                </#if>
                <form method="post" class="text-start">
                  <#if request??>
                    <div class="mb-3">
                      <input type="password" class="form-control" name="password" placeholder="Mot de passe" aria-label="Mot de passe" required>
                    </div>
                    <div class="mb-3">
                      <input type="password" class="form-control" name="password2" placeholder="Confirmez le mot de passe" aria-label="Confirmez le mot de passe" required>
                    </div>
                  <#else>
                    <p>
                      Entrez votre adresse mail UHA. Un mail vous sera envoyé avec un lien afin de créer un nouveau mot de passe.
                    </p>
                    <div class="mb-3">
                      <input type="email" class="form-control" name="email" id="email" required>
                    </div>
                  </#if>
                    <div class="text-center">
                      <input type="submit" class="btn btn-primary w-100 my-4 mb-2" value="Valider">
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
