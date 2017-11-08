import { Host } from './http/host.http.service';

/**
 *
 */
export class Hosts {
  public static isAlive(host: Host): boolean {
    return new Date().getTime() - host.lastSeen < 30 * 1000 * 60;
  }
}
