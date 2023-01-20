<#import "../template.ftl" as template>
<@template.base>
<main class="main-content main-content-bg mt-0">
    <div class="page-header min-vh-100">
      <span class="mask bg-gradient-dark opacity-6"></span>
      <div class="container">
        <div class="row justify-content-center">
          <div class="col-lg-4 col-md-7">
            <div class="card border-0 mb-0">
              <div class="card-body px-lg-5">
                <div class="text-center text-muted mb-4">
                  <h5 class="text-dark text-center mt-2 mb-3">Inscription</h5>
                </div>
                <#if error??>
                  <div class="alert alert-danger" role="alert">${error}</div>
                </#if>
                <form method="post" class="text-start">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="mb-3">
                                <input type="text" class="form-control" name="first_name" placeholder="Prénom" aria-label="Prénom">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="mb-3">
                                <input type="text" class="form-control" name="last_name" placeholder="Nom" aria-label="Nom">
                            </div>
                        </div>
                    </div>
                    <div class="mb-3">
                      <input type="email" class="form-control" name="email" placeholder="Email" aria-label="Email">
                    </div>
                    <div class="row">
                        <div class="col-md-3">
                            <div class="mb-3">
                                <select name="year" class="form-control" aria-label="Année">
                                    <option value="1A">1A</option>
                                    <option value="2A">2A</option>
                                    <option value="3A">3A</option>
                                    <option value="other">4A et plus</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-9">
                            <div class="mb-3">
                                <select name="option" class="form-control" aria-label="Option">
                                    <option value="ir">Informatique et Réseaux</option>
                                    <option value="ase">Automatique et Systèmes embarqués</option>
                                    <option value="meca">Mécanique</option>
                                    <option value="tf">Textile et Fibres</option>
                                    <option value="gi">Génie Industriel</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="mb-3">
                      <input type="password" class="form-control" name="password" placeholder="Mot de passe" aria-label="Mot de passe">
                    </div>
                    <div class="mb-3">
                      <input type="password" class="form-control" name="password2" placeholder="Mot de passe" aria-label="Mot de passe">
                    </div>
                    <div class="text-center">
                      <input type="submit" class="btn btn-primary w-100 my-4 mb-2" value="Inscription">
                    </div>
                    <p class="text-sm mt-3 mb-0">Déjà un compte ? <a href="/account/login" class="text-dark font-weight-bolder">Connexion</a></p>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </main>
</@template.base>
