/**
 * Created by psigloch on 13.01.2017.
 */

export class Validator {

  public static notEmpty(obj:string):boolean {
    return obj && obj.trim().length > 0;
  }

  public static idIsSet(obj:number):boolean {
    return obj && obj > 0
  }
}
