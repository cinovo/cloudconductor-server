import { AbstractControl, ValidatorFn } from '@angular/forms';

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
export class Validator {

  public static notEmpty(obj: string): boolean {
    if (obj) {
      return obj.trim().length > 0;
    }
    return false;
  }

  public static idIsSet(obj: number): boolean {
    return obj && obj > 0
  }

  public static noGtEqZero(obj: number): boolean {
    return obj >= 0;
  }

}

export function gtValidator(n: number): ValidatorFn {
  return (control: AbstractControl): {[key: string]: any} => {
    const smEq = control.value <= n;
    return smEq ? { 'gtValue': { value: control.value } } : null;
  };
}

export function forbiddenNamesValidator(forbiddenNames: string[]): ValidatorFn {
  return (control: AbstractControl): {[key: string]: any} => {
    const forbidden = (forbiddenNames.some((forbiddenName) => forbiddenName === control.value));
    return forbidden ? { 'forbiddenName': { value: control.value } } : null;
  }
}

export function forbiddenNameValidator(forbiddenName: string): ValidatorFn {
  return forbiddenNamesValidator([forbiddenName]);
}
